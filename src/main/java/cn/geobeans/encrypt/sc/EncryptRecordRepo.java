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
    List<EncryptRecord> findByProjectNameContaining(String projectName);
}
