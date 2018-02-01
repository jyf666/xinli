import models.Question;
import org.junit.*;
import service.SpatialMemoryService;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by XIAODA on 2016/5/3.
 */
public class SpatialMemoryServiceTest {

    @Test
    public void makeQuestionCheck() {
        SpatialMemoryService spatialMemoryService = new SpatialMemoryService();
        List<Question> list = spatialMemoryService.makeQuestion("01", 10);
        for (Question question : list) {
            String questionStr = question.getQuestion();
            String[] questrionArr = questionStr.split(";");

            String quesSub0 = questrionArr[0];
            int num = 0;
            for (String quesSub : questrionArr) {
                if(quesSub0.equals(quesSub)){
                    num ++;
                }
            }
            if(num > 1){
                System.out.println(questionStr);
            }
            assertThat(num).isEqualTo(1);
        }
        assertThat(list.size()).isEqualTo(10);
    }
}
