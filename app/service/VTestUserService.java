package service;

import dao.VTestUserDao;
import models.VTestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import persistence.DynamicSpecifications;
import play.mvc.Http;
import utils.PageUtils;

/**
 * Created by XIAODA on 2016/2/22.
 */
@Service
public class VTestUserService {
    @Autowired
    private VTestUserDao vTestUserDao;

    public Page<VTestUser> findAll(Http.Request request, String[] cols){
        Pageable pageable = PageUtils.getPageRequest(request, cols);
        Specification<VTestUser> spec = DynamicSpecifications.fromRequest(request, VTestUser.class);
        return vTestUserDao.findAll(spec, pageable);
    }
}
