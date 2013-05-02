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
          errorsUl = errors.append($('<ul>'))
          $.each createResult.messages.name, (index, message) ->
            errorsUl.append($('<li>').append(message))

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
    new ChatRoom(chatRoomId).musics((musics) ->
      $table = $("##{tableId}")
      $.each musics, (index, music) ->
        $tr = $("<tr data-id=\"#{music.id}\"></tr>")
        $artist = $("<td>#{music.artistName}</td>")
        $album = $("<td>#{music.albumName}</td>")
        $songTitle = $("<td>#{music.songTitle}</td>")
        $table.append(
          $tr
          .append($artist)
          .append($album)
          .append($songTitle)
        )
    , (data) ->
      alert("音楽一覧を取得できませんでした")
      # TODO: ErrorMessage
    )

}