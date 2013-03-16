package controllers

import play.api.mvc.Controller
import jp.t2v.lab.play20.auth.Auth

trait NeedAuthController extends Controller with Auth with AuthConfigImpl