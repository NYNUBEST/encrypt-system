package cn.geobeans.encrypt.sc;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author LiYuFei
 * @create 2019-04-19 11:16
 * @desc 加密记录
 **/
@Data
@Entity
@Table(name = EncryptRecord.TABLE)
public class EncryptRecord implements Serializable {

    public static final String TABLE = "encrypt_record";
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @CreationTimestamp
    @ApiModelProperty(hidden = true)
    private Date createTime;
    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 加密的密码
     */
    private String password;
    /**
     * 加密者姓名
     */
    private String username;

    /**
     * 文件md5值,方便以后文件出现问题后做对比(加密后的文件)
     */
    private String md5;
}
