CREATE TABLE IF NOT EXISTS yonghu
(
    id            VARCHAR(64)  NOT NULL COMMENT '用户ID',
    zhanghao      VARCHAR(32)  NOT NULL COMMENT '登录账号',
    mima          VARCHAR(255) NOT NULL COMMENT '密码哈希',
    dianhua       VARCHAR(32)  NULL COMMENT '手机号',
    shenfenzheng  VARCHAR(32)  NULL COMMENT '大陆身份证号',
    youxiang      VARCHAR(255) NULL COMMENT '邮箱',
    quanxian      TINYINT      NOT NULL DEFAULT 0 COMMENT '权限：0普通用户，1管理员',

    PRIMARY KEY (id),
    UNIQUE KEY uk_yonghu_zhanghao (zhanghao),
    CONSTRAINT ck_yonghu_quanxian
        CHECK (quanxian IN (0, 1))
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = '用户表';

CREATE TABLE IF NOT EXISTS jilu
(
    id                  VARCHAR(64)  NOT NULL COMMENT '记录ID',
    timu                VARCHAR(255) NOT NULL COMMENT '题目',
    ticai               TINYINT      NOT NULL DEFAULT 0 COMMENT '题材：0其他，1日记，2文学，3学术，4会议',
    biaoqian            VARCHAR(100) NULL COMMENT '用于分组的标签，可以为空',
    zhengwen            LONGTEXT     NULL COMMENT '当前正文，可以为空',
    zhuangtai           TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0草稿，1完成',
    yonghu_id           VARCHAR(64)  NOT NULL COMMENT '所属用户ID',
    chuangjian_shijian  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    xiugai_shijian      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
        ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',

    PRIMARY KEY (id),
    KEY idx_jilu_yonghu_xiugai_shijian (yonghu_id, xiugai_shijian),
    CONSTRAINT fk_jilu_yonghu
        FOREIGN KEY (yonghu_id) REFERENCES yonghu (id)
            ON DELETE CASCADE,
    CONSTRAINT ck_jilu_ticai
        CHECK (ticai IN (0, 1, 2, 3, 4)),
    CONSTRAINT ck_jilu_zhuangtai
        CHECK (zhuangtai IN (0, 1))
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = '记录表';

CREATE TABLE IF NOT EXISTS huihua
(
    id                  VARCHAR(64)  NOT NULL COMMENT '会话ID',
    jilu_id             VARCHAR(64)  NOT NULL COMMENT '所属记录ID',
    mingcheng           VARCHAR(100) NOT NULL COMMENT '会话名称',
    chuangjian_shijian  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',

    PRIMARY KEY (id),
    KEY idx_huihua_jilu_id (jilu_id),
    CONSTRAINT fk_huihua_jilu
        FOREIGN KEY (jilu_id) REFERENCES jilu (id)
            ON DELETE CASCADE
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = '会话表';

-- 已有数据库升级时执行一次：为旧会话补充名称和创建时间。
-- 新建数据库无需执行以下两句。
-- ALTER TABLE huihua ADD COLUMN mingcheng VARCHAR(100) NOT NULL DEFAULT '新会话' COMMENT '会话名称';
-- ALTER TABLE huihua ADD COLUMN chuangjian_shijian DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间';

CREATE TABLE IF NOT EXISTS xiaoxi
(
    id                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'AI记忆消息ID',
    huihua_id          VARCHAR(64)  NOT NULL COMMENT '所属会话ID',
    type               VARCHAR(20)  NOT NULL COMMENT '消息类型：USER、ASSISTANT、SYSTEM',
    neirong            LONGTEXT     NOT NULL COMMENT '消息内容',
    chansheng_shijian  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '消息产生时间',

    PRIMARY KEY (id),
    KEY idx_xiaoxi_huihua_id_id (huihua_id, id),
    CONSTRAINT fk_xiaoxi_huihua
        FOREIGN KEY (huihua_id) REFERENCES huihua (id)
            ON DELETE CASCADE,
    CONSTRAINT ck_xiaoxi_type
        CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM'))
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = '会话消息表';
