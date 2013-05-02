class ChatRoom
  create: (options) =>
    @options = options
    name = $('#' + @options.name).val()
    jsRoutes.controllers.ChatRoomsController.create().ajax({
      dataType: 'json',
      data: {'name': name},
    }).done( (createResult) =>
      # success以外の場合はエラーにすべき
      if (createResult.success)
        jsRoutes.controllers.ChatRoomsController.index().ajax({
          dataType: "json",
        }).done( (chatRoomList) =>
          list = $('#' + @options.list)
          list.html('')
          $.each(chatRoomList, (index, element) =>
            li = $('<li>')
            link = $('<a>')
            li.append(link)
            link.attr('tabindex', index)
            link.attr('href', @options.show_url + element.id)
            link.append(element.name)
            list.append(li)
          )
        )
      else
        errors = $('#' + @options.errors)
        errors.html('')
        errorsUl = errors.append($('<ul>'))
        $.each(createResult.messages.name, (index, message) ->
          errorsUl.append($('<li>').append(message))
        )
    ).fail( (data) ->
      alert(data)
    )

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
    new ChatRoom().musics(chatRoomId, tableId)

}