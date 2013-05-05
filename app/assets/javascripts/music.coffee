class Music
  @url: (id, format) ->
    jsRoutes.controllers.MusicsController.show(id, format).absoluteURL()

  @delete: (id) ->
    jsRoutes.controllers.MusicsController.destroy(id).ajax(dataType: "json")

  constructor: (music) ->
    @id = music.id
    @artistName = music.artistName
    @albumName = music.albumName
    @songTitle = music.songTitle
    @fileName = music.fileName
    @format = @fileName.match(/.+(\..+$)/)[1]
    @url = Music.url(@id, @fotmat)

this.Music = Music