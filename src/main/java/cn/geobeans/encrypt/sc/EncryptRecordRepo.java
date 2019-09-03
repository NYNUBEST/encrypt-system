package cn.geobeans.encrypt.sc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author LiYuFei
 * @create 2019-04-19 11:41
 * @desc
 **/
@Repository
public interface EncryptRecordRepo extends JpaRepository<EncryptRecord,String> {

    /**
     * 根据项目名称查询
     * @param projectName 项目名称
     * @return  加密记录实体
     */
    List<EncryptRecord> findAllByProjectNameContaining(String projectName);
}
