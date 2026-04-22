-- 插入测试提醒日志记录数据
-- 当前时间：2026-04-11
-- 农户：id=1(王大山), id=2(刘富贵)

-- 1. 给刘富贵的到期前提醒
INSERT INTO reminder_log (
    farmer_id, store_id, reminder_type, reminder_date,
    message_content, notification_channels, send_status
) VALUES (
    2, 1, 'UPCOMING', CURDATE(),
    '尊敬的刘富贵，您的赊销订单（SO20260408001）将在 7 天后到期，应还金额 1680 元，请提前准备资金。',
    '["APP"]', 'SENT'
);

-- 2. 给王大山的春季提醒
INSERT INTO reminder_log (
    farmer_id, store_id, reminder_type, reminder_date,
    message_content, notification_channels, send_status
) VALUES (
    1, 1, 'SEASONAL', '2026-04-01',
    '尊敬的王大山，春季已到，请及时核对并结清农资赊销款项，为春耕生产做好准备。',
    '["APP","SMS"]', 'SENT'
);

-- 3. 给刘富贵的到期前 1 天提醒
INSERT INTO reminder_log (
    farmer_id, store_id, reminder_type, reminder_date,
    message_content, notification_channels, send_status
) VALUES (
    2, 1, 'UPCOMING', '2026-04-24',
    '【明日到期】刘富贵，您的赊销订单（SO20260408001）将于明天到期，金额 1680 元，请务必准备好资金！',
    '["APP","SMS"]', 'PENDING'
);

-- 4. 给王大山的最新提醒
INSERT INTO reminder_log (
    farmer_id, store_id, reminder_type, reminder_date,
    message_content, notification_channels, send_status
) VALUES (
    1, 1, 'UPCOMING', CURDATE(),
    '尊敬的王大山，您的赊销订单（SO20260401001）还有 51 天到期，当前欠款 2500 元，请合理安排资金。',
    '["APP"]', 'SENT'
);
