class ChatRoom
  constructor: (options) ->
    @options = options

  create: =>
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

root = exports ? this
root.ChatRoomActions = {
  bindCreate: (options) ->
    $('#' + options.button).click(->
      new ChatRoom(options).create()
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

}