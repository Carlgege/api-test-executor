package com.jollychic.exec;

import com.jollychic.bean.ResponseAssert;
import okhttp3.Response;

import java.util.List;

public interface IAPITestExec {

    void updateRequestWithSql();

    void updateRequestWithRegular();

    void updateRequestWithReferenceCase();

    void updateRequestUrl();

    void responseCodeAssert(Response response);

    void responseValueAssert(List<ResponseAssert> responseAssertList, String resBody);

}
