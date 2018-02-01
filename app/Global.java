
import com.jolbox.bonecp.BoneCPDataSource;
import controllers.LoginController;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.DateTimeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import play.Application;
import play.Play;
import play.data.format.Formatters;
import play.mvc.*;
import play.libs.F.*;
import service.LoginService;
import utils.ApplicationContextUtils;
import utils.DateUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import static play.mvc.Results.*;
/**
 * Created by XIAODA on 2015/12/16.
 */
public class Global extends play.GlobalSettings{

	private static Logger logger = LoggerFactory.getLogger(Global.class);

	@Override
	public void onStart(Application app) {
        logger.debug("初始化spring上下文");
        ApplicationContextUtils.init();

        logger.debug("注册类型转换器");
        DateTimeConverter dtConverter = new DateConverter();
        dtConverter.setPatterns(new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss"});
        ConvertUtils.register(dtConverter, Date.class);

		logger.debug("注册表单绑定Date类型时的转换规则");
		Formatters.register(Date.class, new Formatters.SimpleFormatter<Date>() {
			@Override
			public Date parse(String input, Locale l) throws ParseException {
				try {
					Long millis = Long.valueOf(input);
					return new Date(millis);
				} catch (Exception e) {
					return DateUtils.parseDate(input);
				}
			}

			@Override
			public String print(Date date, Locale l) {
				return DateUtils.formatDate(date);
			}
		});
	}

	@Override
	public void onStop(Application app) {
		ApplicationContext applicationContext = ApplicationContextUtils.getApplicationContext();
		try {
			logger.debug("关闭数据源。");
			if (applicationContext != null && applicationContext.getBean(DataSource.class) != null) {
				applicationContext.getBean(BoneCPDataSource.class).close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * required to fetch controllers from spring (in usual play app this is not required)
	 */
	@Override
	public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
		return ApplicationContextUtils.getBean(controllerClass);
	}

	@Override
	public Action onRequest(final Http.Request var1, Method var2) {
		Boolean devphase = Play.application().configuration().getBoolean("system.devphase");

		if (Boolean.TRUE == devphase || var2.getDeclaringClass() == LoginController.class) {
			return super.onRequest(var1, var2);
		}
		final String uriPrefix = var1.uri().split("/")[1];
		if (uriPrefix.equals("public") || "/org/reg".equals(var1.uri())) {
			return super.onRequest(var1, var2);
		}

		final LoginService loginService = ApplicationContextUtils.getBean(LoginService.class);
		return new Action.Simple() {
			public Promise<Result> call(Http.Context ctx) throws Throwable {
				if (loginService.validAdminLogin() || loginService.validUserLogin()) {
					return delegate.call(ctx);
				} else {
					if (uriPrefix.equals("admin") || uriPrefix.equals("user") || uriPrefix.equals("test") || uriPrefix.equals("score") || uriPrefix.equals("org") || uriPrefix.equals("testpaper")
							|| uriPrefix.equals("question") || uriPrefix.equals("questiontype") || uriPrefix.equals("manager")|| uriPrefix.equals("orders")) {
						return Promise.pure(redirect("/admin/login"));
					} else {
						return Promise.pure(redirect("/"));
					}
				}
			}
		};
	}

	@Override
	public Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
		return Promise.<Result>pure(notFound(
			views.html.error_404.render(request.method(), request.uri())
		));
	}
}
