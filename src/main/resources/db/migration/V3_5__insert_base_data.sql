-- 插入基础数据：农户、店铺、用户
-- 当前时间：2026-04-11
-- 说明：如果数据库中已有这些数据，请跳过此文件

-- ==================== 1. 插入农户数据 ====================
INSERT INTO farmer (
    farmer_code, farmer_name, nickname, phone, 
    address, village, total_credit_limit, total_debt, 
    credit_score, credit_level, remark, status
) VALUES 
    ('FM20260101001', '王大山', '大山', '13511111111', 
     '河南省郑州市金水区王庄村', '王庄村三组', 5000.00, 1500.00, 
     85, 'GOOD', '种植大户，主要种植小麦、玉米，信用良好', 1),
    ('FM20260101002', '刘富贵', '富贵', '13522222222', 
     '河南省郑州市郑东新区刘庄', '刘庄村二组', 8000.00, 3200.00, 
     75, 'NORMAL', '合作社负责人，种植经济作物，偶尔有逾期', 1),
    ('FM20260101003', '李老三', '老三', '13533333333', 
     '河南省郑州市金水区李庄', '李庄村一组', 3000.00, 2700.00, 
     60, 'POOR', '信用较差，经常逾期', 1);

-- ==================== 2. 插入店铺数据 ====================
INSERT INTO store (
    store_name, store_code, owner_id, address, phone, 
    contact_person, description, status
) VALUES 
    ('张记农资店', 'ZHANG001', 1, '河南省郑州市金水区农业路 1 号', '0371-88888888', 
     '张老板', '专业销售化肥、农药、种子，服务周边农户 20 年', 1);

-- ==================== 3. 插入农户 - 店铺关联数据 ====================
INSERT INTO farmer_store_rel (
    farmer_id, store_id, credit_limit, current_debt, 
    total_transactions, total_transaction_amount, 
    is_primary, relationship_type, remark, status
) VALUES 
    (1, 1, 3000.00, 800.00, 15, 45000.00, 1, 'REGULAR', '老客户，主要采购化肥和农药', 1),
    (2, 1, 5000.00, 2500.00, 25, 65000.00, 1, 'COOPERATIVE', '合作社批量采购，有账期', 1),
    (3, 1, 2000.00, 700.00, 8, 18000.00, 0, 'REGULAR', '普通客户', 1);
