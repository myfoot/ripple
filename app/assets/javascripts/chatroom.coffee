class ChatRoom
  create: (options) =>
    @options = options
    name = $('#' + @options.name).val()
    jsRoutes.controllers.ChatRoomsController.create().ajax({
      dataType: 'json',
      data: {'name': name},
      success: (data) =>
        if (data.success)
          jsRoutes.controllers.ChatRoomsController.index().ajax({
            dataType: "json",
            success: (data) =>
              list = $('#' + @options.list)
              list.html('')
              $.each(data, (index, element) =>
                li = $('<li>')
                link = $('<a>')
                li.append(link)
                link.attr('tabindex', index)
                link.attr('href', @options.show_url + element.id)
                link.append(element.name)
                list.append(li)
              )
          })
        else
          errors = $('#' + @options.errors)
          errors.html('')
          errorsUl = errors.append($('<ul>'))
          $.each(data.messages.name, (index, message) ->
            errorsUl.append($('<li>').append(message))
          )
      error: (data) ->
        alert(data)
    })

  musics: (chatRoomId, tableId) =>
    jsRoutes.controllers.MusicsController.index(chatRoomId).ajax({
      dataType: "json"
    }).done( (data) =>
      $table = $("##{tableId}")
      console.log data.musics
      $.each(data.musics, (index, music) =>
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
      )
    ).fail( (data) ->
      alert("音楽一覧が取得できませんでした")
    )

root = exports ? this
root.ChatRoomActions = {
  bindCreate: (options) ->
    $('#' + options.button).click(->
      new ChatRoom().create(options)
    )

  bindAjaxFilePost: (id) ->
    $form = $("##{id}")
    $form.submit(=>
      fd = new FormData($form[0])
      $.ajax($form.attr("action"), {
        type: 'post',
        processData: false,
        contentType: false,
        data: fd,
        dataType: 'html',
        success: (data) ->
          console.log(data)
        error: (xhr, status, error) ->
          console.log("error");
          console.log(xhr.responseText);
          console.log(status);
          console.log(error);
      })
      false
    )

  showMusics: (chatRoomId, tableId) ->
    new ChatRoom().musics(chatRoomId, tableId)

}