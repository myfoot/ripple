class ChatRoom
  constructor: (options) ->
    @options = options

  create: =>
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

root = exports ? this
root.ChatRoomActions = {
  bindCreate: (options) ->
    $('#' + options.button).click(->
      new ChatRoom(options).create()
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

}