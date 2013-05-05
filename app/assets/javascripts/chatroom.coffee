class ChatRoom
  @create: (name, sucessHandler, failedHandler) ->
    jsRoutes.controllers.ChatRoomsController.create().ajax({
      dataType: 'json',
      data: {'name': name},
    }).done( (createResult) ->
      # success以外の場合はエラーにすべき
      sucessHandler(createResult)
    ).fail( (data) ->
      alert(data)
      failedHandler(data)
    )

  @all: (successHandler) ->
    jsRoutes.controllers.ChatRoomsController.index().ajax({
        dataType: "json",
    }).done( (chatRoomList) ->
      successHandler(chatRoomList)
    )

  constructor: (chatRoomId) ->
    @id = chatRoomId

  musics: (successHandler, failedHandler) =>
    jsRoutes.controllers.MusicsController.index(@id).ajax({
      dataType: "json"
    }).done( (data) ->
      successHandler(data.musics)
    ).fail( (data) ->
      failedHandler(data)
    )

root = exports ? this
root.ChatRoomActions = {
  bindCreate: (options) ->
    $('#' + options.button).click ->
      name = $('#' + options.name).val()
      ChatRoom.create name, (createResult) ->
        if createResult.success
          ChatRoom.all((chatRooms) ->
            list = $('#' + options.list)
            list.html('')
            $.each chatRooms, (index, element) ->
              li = $('<li>')
              link = $('<a>')
              li.append(link)
              link.attr('tabindex', index)
              link.attr('href', options.show_url + element.id)
              link.append(element.name)
              list.append(li)
          )
        else
          errors = $('#' + options.errors)
          errors.html('')
          errorsDiv = $('<div class="alert alert-error">')
          errorsUl = errorsDiv.append($('<ul>'))
          $.each createResult.messages.name, (index, message) ->
            errorsUl.append($('<li>').append(message))
          errors.append(errorsDiv)

  bindAjaxFilePost: (id) ->
    $form = $("##{id}")
    $form.submit( =>
      fd = new FormData($form[0])
      $.ajax($form.attr("action"), {
        type: 'post',
        processData: false,
        contentType: false,
        data: fd,
        dataType: 'html',
      }).done( (data) ->
        console.log(data)
      ).fail( (xhr, status, error) ->
        console.log("error")
        console.log(xhr.responseText)
        console.log(status)
        console.log(error)
      )
      false
    )

  showMusics: (chatRoomId, tableId) ->
    $dfd = $.Deferred()
    setTimeout(->
      new ChatRoom(chatRoomId).musics((musics) ->
        $table = $("##{tableId}")
        $tbody = $("<tbody></tbody>")
        $table.append($tbody)
        $.each musics, (index, musicJson) ->
          music = new Music(musicJson)
          $tr = $("<tr data-id=\"#{music.id}\" data-format=\"#{music.format}\"></tr>")
          $artist = $("<td>#{music.artistName}</td>")
          $album = $("<td>#{music.albumName}</td>")
          $songTitle = $("<td>#{music.songTitle}</td>")
          $tbody.append(
            $tr
            .append($artist)
            .append($album)
            .append($songTitle)
          )
        $dfd.resolve()
      , (data) ->
        alert("音楽一覧を取得できませんでした")
        # TODO: ErrorMessage
        $dfd.reject()
      )
    , 0)
    $dfd.promise()

  playAllMusic: (controlsId, tableId) =>
    musics = $("##{tableId} tbody tr").map((index, tr) -> id: $(tr).data('id'), format: $(tr).data('format'))
    if musics.length > 0
      $controls = $("##{controlsId}")
      $audio = $("<audio controls></audio>")
      $controls.append($audio)

      count = 0
      $audio.attr "src", Music.url(musics[count].id, musics[count].format)
      $audio[0].play()
      $audio[0].addEventListener "ended", (event) ->
        if ++count >= musics.length
          count = 0
        $audio.attr "src", Music.url(musics[count].id, musics[count].format)
        $audio[0].load()
        $audio[0].play()
}