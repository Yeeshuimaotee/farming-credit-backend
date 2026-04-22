-- 插入测试还款记录数据
-- 当前时间：2026-04-11
-- 配合 V4__insert_test_orders.sql 使用
-- 订单 ID：49-55
-- 农户：id=1(王大山), id=2(刘富贵)

-- 1. 刘富贵 - SO20260405001 的部分还款记录（订单 ID=50）
INSERT INTO repayment (
    repayment_no, order_id, farmer_id, store_id, repayment_date, 
    repayment_amount, repayment_type, remark, operator_id, operator_name
) VALUES (
    'RP20260415001', 50, 2, 1, '2026-04-15', 1000.00,
    'WECHAT', '第一次还款', 1, '张老板'
);

-- 2. 王大山 - SO20260320001 的还款记录（已还清，订单 ID=51）
INSERT INTO repayment (
    repayment_no, order_id, farmer_id, store_id, repayment_date,
    repayment_amount, repayment_type, remark, operator_id, operator_name
) VALUES (
    'RP20260418001', 51, 1, 1, '2026-04-18', 850.00,
    'BANK_TRANSFER', '一次性还清', 1, '张老板'
);

-- 3. 王大山 - SO20260401001 的部分还款记录（订单 ID=53）
INSERT INTO repayment (
    repayment_no, order_id, farmer_id, store_id, repayment_date,
    repayment_amount, repayment_type, remark, operator_id, operator_name
) VALUES (
    'RP20260410001', 53, 1, 1, '2026-04-10', 2000.00,
    'CASH', '春季订单首付款', 1, '张老板'
);

-- 4. 刘富贵 - 另一笔还款（给 SO20260405001，订单 ID=50）
INSERT INTO repayment (
    repayment_no, order_id, farmer_id, store_id, repayment_date,
    repayment_amount, repayment_type, remark, operator_id, operator_name
) VALUES (
    'RP20260420001', 50, 2, 1, '2026-04-20', 500.00,
    'ALIPAY', '第二次还款', 1, '张老板'
);

-- 5. 王大山 - 最新还款记录（给 SO20260401001，订单 ID=53）
INSERT INTO repayment (
    repayment_no, order_id, farmer_id, store_id, repayment_date,
    repayment_amount, repayment_type, remark, operator_id, operator_name
) VALUES (
    'RP20260411001', 53, 1, 1, CURDATE(), 500.00,
    'CASH', '最新还款', 1, '张老板'
);
