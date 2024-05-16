/*
我已经黑转粉了，我是正规军
                                ⠀⠀⠀ ⠀⠰⢷⢿⠄
                                ⠀⠀⠀⠀ ⠀⣼⣷⣄
                                ⠀ ⠀⣤⣿⣇⣿⣿⣧⣿⡄
                                ⢴⠾⠋⠀⠀⠻⣿⣷⣿⣿⡀
                                🏀    ⢀⣿⣿⡿⢿⠈⣿
                                ⠀⠀⠀ ⢠⣿⡿⠁⠀⡊⠀⠙
                                ⠀ ⠀⠀⢿⣿⠀⠀⠹⣿
                                ⠀⠀ ⠀⠀⠹⣷⡀⠀⣿⡄
🐔作者：芥末喂泡泡糖
*/
package com.yupi.oj.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.datasource.*;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.model.dto.search.SearchAllQueryRequest;
import com.yupi.oj.model.entity.Picture;
import com.yupi.oj.model.enums.SearchTypeEnum;
import com.yupi.oj.model.vo.PostVO;
import com.yupi.oj.model.vo.QuestionVO;
import com.yupi.oj.model.vo.SearchVO;
import com.yupi.oj.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName SearchFacade 搜索蒙门面
 * @Description TODO
 * @Author LXY
 * @Date 2024/3/3 14:30
 * @Version 1.0
 */
@Slf4j
@Component
public class SearchFacade {


    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private QuestionDataSource questionDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    /**
     * 分页获取列表（封装类）
     *
     * @param searchAllRequest
     * @param request
     * @return
     */

    public SearchVO searchAllVoList(@RequestBody SearchAllQueryRequest searchAllRequest, HttpServletRequest request) {

        String type = searchAllRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        String searchText = searchAllRequest.getSearchText();
        long current = searchAllRequest.getCurrent();
        long pageSize = searchAllRequest.getPageSize();


        // 搜索出所有数据
        if (searchTypeEnum == null) {
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> userDataSource.doSearch(searchText, current, pageSize, request));
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> postDataSource.doSearch(searchText, current, pageSize, request));
            CompletableFuture<Page<QuestionVO>> questionTask = CompletableFuture.supplyAsync(() -> questionDataSource.doSearch(searchText, current, pageSize, request));
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> pictureDataSource.doSearch(searchText, current, pageSize, request));
            CompletableFuture.allOf(userTask, postTask, questionTask, pictureTask).join();
            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                Page<QuestionVO> questionVOPage = questionTask.get();
                SearchVO searchVO = new SearchVO();
                searchVO.setUserVOList(userVOPage);
                searchVO.setPostVOList(postVOPage);
                searchVO.setPictureList(picturePage);
                searchVO.setQuestionVOList(questionVOPage);
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize, request);
            switch (searchTypeEnum) {
                case USER -> {
                    searchVO.setUserVOList((Page<UserVO>) page);
                }
                case PICTURE -> {
                    searchVO.setPictureList((Page<Picture>) page);
                }
                case QUESTION -> {
                    searchVO.setQuestionVOList((Page<QuestionVO>) page);
                }
                case POST -> {
                    searchVO.setPostVOList((Page<PostVO>) page);
                }
            }
            return searchVO;
        }
    }


}
