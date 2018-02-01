package utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import play.libs.Json;
import play.mvc.Http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAODA on 2015/12/25.
 */
public class PageUtils {

    public static PageRequest getPageRequest(Http.Request request, String[] cols){
        int start = Integer.parseInt(request.getQueryString("start"));//数据起始位置
        int pageSize = Integer.parseInt(request.getQueryString("length"));//数据长度
        String orderColumn = request.getQueryString("order[0][column]");//获取客户端需要那一列排序
        orderColumn = cols[Integer.parseInt(orderColumn)];
        String orderDir = request.getQueryString("order[0][dir]");//获取排序方式 默认为asc
        int pageNum = start/pageSize;// 第几页
        return new PageRequest(pageNum, pageSize, new Sort(Sort.Direction.fromString(org.apache.commons.lang3.StringUtils.upperCase(orderDir)), orderColumn));
    }

    /**
     * 将Page对象转换成tableData做需要的json数据
     * @param page
     * @param draw
     * @return
     */
    public static JsonNode convertToTableData(Page<?> page, String draw){
        List data = page.getContent();
        Long recordsTotal = page.getTotalElements();

        Map<Object, Object> tableData = new HashMap<Object, Object>();
        tableData.put("data", data);
        tableData.put("recordsTotal", recordsTotal);//总记录数
        tableData.put("recordsFiltered", recordsTotal);//过滤后记录数
        tableData.put("draw", draw);
        System.out.println(tableData);
        return Json.toJson(tableData);
    }
}
