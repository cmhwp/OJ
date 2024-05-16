
package com.yupi.oj.controller;


import com.yupi.oj.common.BaseResponse;
import com.yupi.oj.common.ResultUtils;
import com.yupi.oj.manager.SearchFacade;
import com.yupi.oj.model.dto.search.SearchAllQueryRequest;
import com.yupi.oj.model.vo.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 图片接口
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAllVoList(@RequestBody SearchAllQueryRequest searchAllRequest, HttpServletRequest request) {

        SearchVO searchVO = searchFacade.searchAllVoList(searchAllRequest, request);

        return ResultUtils.success(searchVO);
    }


}






