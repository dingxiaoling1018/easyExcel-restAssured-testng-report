package com.saber.api.autotest.utils;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.saber.api.autotest.model.TestCaseModel;
import java.util.ArrayList;
import java.util.List;

public class ExcelListener extends AnalysisEventListener<TestCaseModel> {

    //自定义用于暂时存储data。
    //可以通过实例获取该值
    private List<TestCaseModel> datas = new ArrayList<TestCaseModel>();
    @Override
    public void invoke(TestCaseModel object, AnalysisContext context) {
        System.out.println("当前行："+context.getCurrentRowNum());
        System.out.println(object);
        datas.add(object);//数据存储到list，供批量处理，或后续自己业务逻辑处理。
        doSomething(object);//根据自己业务做处理
    }
    private void doSomething(Object object) {
        //1、入库调用接口
    }



    public void doAfterAllAnalysed(AnalysisContext context) {
        // datas.clear();//解析结束销毁不用的资源
    }
    public List<TestCaseModel> getDatas() {
        return datas;
    }
    public void setDatas(List<TestCaseModel> datas) {
        this.datas = datas;
    }


}