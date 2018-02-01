/*
 * Copyright 2012 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package security;

import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import dao.AdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import service.SessionService;

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
public class BuggyDeadboltHandler extends AbstractDeadboltHandler {

    @Autowired
    private AdminDao adminDao;

    public F.Promise<Result> beforeAuthCheck(Http.Context context)
    {
        // returning null means that everything is OK.  Return a real result if you want a redirect to a login page or
        // somewhere else
        return F.Promise.pure(null);
    }

    public F.Promise<Subject> getSubject(final Http.Context context)
    {
        // in a real application, the user name would probably be in the session following a login process
        return F.Promise.promise(new F.Function0<Subject>() {
            @Override
            public Subject apply() throws Throwable {
//                return AuthorisedUser.findByUserName("steve");

                Integer adminId = Integer.parseInt(context.session().get(SessionService.SessionItemMark.ADMIN_ID.name()));
                return adminDao.findOne(adminId);
            }
        });
    }

    public DynamicResourceHandler getDynamicResourceHandler(Http.Context context)
    {
        return new MyDynamicResourceHandler();
    }

    @Override
    public F.Promise<Result> onAuthFailure(Http.Context context,
                                           String content)
    {
        throw new RuntimeException("An exception occurred in onAuthFailure");
    }
}
