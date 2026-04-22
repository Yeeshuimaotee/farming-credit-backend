-- 插入测试订单数据（2026 年近期订单）
-- 当前时间：2026-04-11
-- 数据库现有数据：
--   - 农户：id=1(王大山), id=2(刘富贵)
--   - 店铺：需要先确认是否有数据

-- 1. 王大山 - 待审核订单
INSERT INTO credit_order (
    order_no, store_id, farmer_id, order_date, due_date, total_amount, 
    paid_amount, debt_amount, order_status, payment_status, 
    seasonal_flag, invoice_type, remark, creator_id, creator_name, create_time, update_time
) VALUES (
    'SO20260410001', 1, 1, '2026-04-10', '2026-05-10', 1500.00,
    0.00, 1500.00, 'PENDING', 'UNPAID',
    0, 'NORMAL', '春耕化肥采购', 1, '张老板', NOW(), NOW()
);

-- 2. 刘富贵 - 已审核订单（部分还款）
INSERT INTO credit_order (
    order_no, store_id, farmer_id, order_date, due_date, total_amount,
    paid_amount, debt_amount, order_status, payment_status,
    seasonal_flag, invoice_type, remark, creator_id, creator_name, 
    approver_id, approver_name, approve_time, create_time, update_time
) VALUES (
    'SO20260405001', 1, 2, '2026-04-05', '2026-05-05', 2800.00,
    1000.00, 1800.00, 'APPROVED', 'PARTIAL',
    0, 'NORMAL', '农药和种子采购', 1, '张老板',
    1, '张老板', NOW(), NOW(), NOW()
);

-- 3. 王大山 - 已还清订单
INSERT INTO credit_order (
    order_no, store_id, farmer_id, order_date, due_date, total_amount,
    paid_amount, debt_amount, order_status, payment_status,
    seasonal_flag, invoice_type, remark, creator_id, creator_name,
    approver_id, approver_name, approve_time, create_time, update_time
) VALUES (
    'SO20260320001', 1, 1, '2026-03-20', '2026-04-20', 850.00,
    850.00, 0.00, 'PAID_OFF', 'PAID',
    0, 'NORMAL', '小型农具采购', 1, '张老板',
    1, '张老板', '2026-03-21 09:00:00', '2026-03-20 16:20:00', '2026-04-18 10:00:00'
);

-- 4. 刘富贵 - 即将到期订单
INSERT INTO credit_order (
    order_no, store_id, farmer_id, order_date, due_date, total_amount,
    paid_amount, debt_amount, order_status, payment_status,
    seasonal_flag, invoice_type, remark, creator_id, creator_name,
    approver_id, approver_name, approve_time, create_time, update_time
) VALUES (
    'SO20260408001', 1, 2, '2026-04-08', '2026-04-25', 1680.00,
    0.00, 1680.00, 'APPROVED', 'UNPAID',
    0, 'NORMAL', '玉米种子', 1, '张老板',
    1, '张老板', '2026-04-09 08:30:00', '2026-04-08 11:15:00', NOW()
);

-- 5. 王大山 - 季节性订单（春季）
INSERT INTO credit_order (
    order_no, store_id, farmer_id, order_date, due_date, total_amount,
    paid_amount, debt_amount, order_status, payment_status,
    seasonal_flag, invoice_type, remark, creator_id, creator_name,
    approver_id, approver_name, approve_time, create_time, update_time
) VALUES (
    'SO20260401001', 1, 1, '2026-04-01', '2026-06-01', 4500.00,
    2000.00, 2500.00, 'APPROVED', 'PARTIAL',
    1, 'NORMAL', '春季大批量采购', 1, '张老板',
    1, '张老板', '2026-04-02 10:00:00', '2026-04-01 09:30:00', NOW()
);

-- 6. 刘富贵 - 待审核订单
INSERT INTO credit_order (
    order_no, store_id, farmer_id, order_date, due_date, total_amount,
    paid_amount, debt_amount, order_status, payment_status,
    seasonal_flag, invoice_type, remark, creator_id, creator_name, create_time, update_time
) VALUES (
    'SO20260414001', 1, 2, '2026-04-14', '2026-05-14', 920.00,
    0.00, 920.00, 'PENDING', 'UNPAID',
    0, 'NORMAL', '蔬菜种子和农药', 1, '张老板', NOW(), NOW()
);

-- 7. 王大山 - 今日订单
INSERT INTO credit_order (
    order_no, store_id, farmer_id, order_date, due_date, total_amount,
    paid_amount, debt_amount, order_status, payment_status,
    seasonal_flag, invoice_type, remark, creator_id, creator_name, create_time, update_time
) VALUES (
    'SO20260411001', 1, 1, '2026-04-11', '2026-05-11', 680.00,
    0.00, 680.00, 'PENDING', 'UNPAID',
    0, 'NORMAL', '今日采购 - 农药', 1, '张老板', NOW(), NOW()
);
