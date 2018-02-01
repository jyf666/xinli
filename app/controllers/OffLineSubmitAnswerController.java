package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.localStorage.answerList;


@org.springframework.stereotype.Controller
public class OffLineSubmitAnswerController extends Controller {

    public Result showOffLineAnswer(){
        return ok(answerList.render());
    }
}
