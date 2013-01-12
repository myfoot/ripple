var ChatRoomActions = {
    create: function(options){
        var name = $('#' + options.name).val();
        jsRoutes.controllers.ChatRoomsController.create().ajax({
            dataType: 'json',
            data: {'name': name},
            success: function(data) {
                if (data.success) {
                    jsRoutes.controllers.ChatRoomsController.index().ajax({
                        dataType: "json",
                        success: function(data) {
                            var list = $('#' + options.list);
                            list.html('');
                            $.each(data, function(index, element){
                                var li = $('<li>');
                                var link = $('<a>');
                                li.append(link);
                                link.attr('tabindex', index);
                                link.attr('href', showUrl + element.id);
                                link.append(element.name);
                                list.append(li);
                            });
                        }
                    });
                } else {
                    var errors = $('#' + options.errors);
                    errors.html('');
                    var errorsUl = errors.append($('<ul>'));
                    $.each(data.messages.name, function(index, message){ errorsUl.append($('<li>').append(message)) });
                }
            },
            error: function(data) {
                alert(data);
            }
        });
    },
    bindCreate: function(options) {
        $('#' + options.button).click(function(){ChatRoomActions.create(options)});
    }
};

