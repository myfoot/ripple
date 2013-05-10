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

class MusicHelper
  @musicListRow: (music, deleteHandler) ->
    $controls = $("<td></td>")
    $deleteButton = $("<button title=\"delete this music\">×</button>")
    $deleteButton.bind "click", (event) ->
      Music.delete(music.id).always(deleteHandler)
    $controls.append($deleteButton)

    $("<tr data-id=\"#{music.id}\" data-format=\"#{music.format}\"></tr>")
      .append($("<td>#{music.artistName}</td>"))
      .append($("<td>#{music.albumName}</td>"))
      .append($("<td>#{music.songTitle}</td>"))
      .append($controls)

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
          errorsUl = $('<ul>')
          errorsDiv.append(errorsUl)
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
        $("##{tableId} tbody").remove()
        $table = $("##{tableId}")
        $tbody = $("<tbody></tbody>")
        $table.append($tbody)
        $.each musics, (index, musicJson) ->
          music = new Music(musicJson)
          $tbody.append(MusicHelper.musicListRow(music, (json) ->
            alert(json.message) if json.success
            ChatRoomActions.showMusics(chatRoomId, tableId)
          ))
        $dfd.resolve()
      , (data) ->
        alert("音楽一覧を取得できませんでした")
        # TODO: ErrorMessage
        $dfd.reject()
      )
    , 0)
    $dfd.promise()

  createMusicInformations: (music) ->


  playAllMusic: (controlsId, tableId) =>
    musics = $("##{tableId} tbody tr").map((index, tr) ->
      td = $(tr).find("td")
      id: $(tr).data('id'), format: $(tr).data('format'), song: td.eq(2).html(), artist: td.eq(0).html(), album: td.eq(1).html())
    if musics.length > 0
      # 出来上がるHTML構造
      # <div id="controls" class="navbar">
      #   <div class="navbar-inner">
      #     <p>Title</p>
      #     <div>Artist - Album</div>
      #     <audio controls="" src="http://localhost:9000/musics/1.m4a"></audio>
      #   </div>
      # </div>
      $controls = $("##{controlsId}")
      $navbarInner = $("<div class=\"navbar-inner\"></div>")
      $controls.append($navbarInner)

      count = 0
      $song = $("<p>#{musics[count].song}</p>")
      $navbarInner.append($song)
      $artistAndAlbum = $("<div>#{musics[count].artist} - #{musics[count].album}</div>")
      $navbarInner.append($artistAndAlbum)
      $audio = $("<audio controls></audio>")
      $navbarInner.append($audio)
      $audio.attr "src", Music.url(musics[count].id, musics[count].format)
      $audio[0].play()
      $audio[0].addEventListener "ended", (event) ->
        if ++count >= musics.length
          count = 0
        $song.text(musics[count].song)
        $artistAndAlbum.text("#{musics[count].artist} - #{musics[count].album}")
        $audio.attr "src", Music.url(musics[count].id, musics[count].format)
        $audio[0].load()
        $audio[0].play()
}