/**
 * Created by XIAODA on 2016/4/27.
 */
/**
 * @param qtype 题型
 * @param tid 考试id
 */
function forwardByQtype(qtype, tid){
    var url = getTestUrl(qtype);
    location.replace(url + "?tid=" + tid);// 材料记忆
}

function getTestUrl(qtype){
    switch (qtype){
        case 1:
            return '/materialMemory/instructions';// 材料记忆
        case 2:
            return '/symbolicOperation/instructions';// 符号运算
        case 3:
            return '/spatialMemory/instructions';// 顺序记忆
        case 4:
            return '/grapfSearch/instructions';// 图案搜索
        case 5:
            return '/shapelinking/instructions';// 图形连线
        case 6:
            return '/paragraphReasoning/instructions';// 段落推理
        case 7:
            return '/memoryExtract/instructions';// 材料提取
        case 8:
            return '/analogicReasoning/instructions';// 类比推理
        case 9:
            return '/matrixReasoning/instructions';// 矩阵推理
        case 10:
            return '/personality/instructions';// 人格测试
        case 11:
            return '/familyQuestionnaire/instructions';// 家庭情况
        case 12:
            return '/spaceRotation/instructions';// 空间旋转
        case 13:
            return '/emotionRecognition/instructions';// 情绪识别
        case 14:
            return '/emotionUnderstanding/instructions';// 情绪理解
        case 15:
            return '/remoteAssociation/instructions';// 远距离联想
        case 16:
            return '/criticalThinking/ability/home/instructions';// 批判性思维-能力
        case 17:
            return '/criticalThinking/tendency/instructions';// 批判性思维-倾向
        default:
            return '/index/forwardTest/0';
    }
}