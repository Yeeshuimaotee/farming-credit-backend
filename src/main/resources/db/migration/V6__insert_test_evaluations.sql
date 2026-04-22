-- 插入测试信用评估记录数据
-- 当前时间：2026-04-11
-- 农户：id=1(王大山), id=2(刘富贵)

-- 1. 王大山 - 良好信用记录
INSERT INTO credit_evaluation (
    farmer_id, store_id, evaluation_date, evaluation_type,
    score, level, repayment_rate, avg_overdue_days, total_overdue_count,
    total_order_count, total_transaction_amount, annual_credit_amount,
    credit_frequency, active_month_ratio, recommended_credit_limit,
    improvement_suggestions, evaluator_id, evaluator_name
) VALUES (
    1, 1, '2026-04-01', 'AUTO',
    85, 'GOOD', 95.00, 2.5, 1,
    8, 23000.00, 12000.00,
    6, 75.00, 3500.00,
    '还款及时，可适当提高信用额度', 1, '张老板'
);

-- 2. 刘富贵 - 一般信用记录
INSERT INTO credit_evaluation (
    farmer_id, store_id, evaluation_date, evaluation_type,
    score, level, repayment_rate, avg_overdue_days, total_overdue_count,
    total_order_count, total_transaction_amount, annual_credit_amount,
    credit_frequency, active_month_ratio, recommended_credit_limit,
    risk_factors, improvement_suggestions, evaluator_id, evaluator_name
) VALUES (
    2, 1, '2026-04-05', 'MANUAL',
    75, 'NORMAL', 80.00, 15.5, 3,
    12, 42000.00, 18000.00,
    9, 85.00, 4500.00,
    '["overdue_frequency", "large_debt"]',
    '建议加强还款提醒，控制单次赊销额度', 1, '张老板'
);
