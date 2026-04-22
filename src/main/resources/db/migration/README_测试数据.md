# 数据库测试数据插入说明

## 📋 概述

本目录包含用于插入测试数据的 SQL 脚本，模拟真实的赊销业务场景。

## 📁 数据文件列表

### V4__insert_recent_orders.sql
**插入近期赊销订单记录**
- 10 条订单数据
- 包含各种状态：待审核、已审核、已逾期、已还清、已拒绝、已取消
- 涉及 3 个农户：王大山、刘富贵、李老三
- 店铺 ID：1（张记农资店）
- 订单日期：2025 年 3 月 -4 月

### V5__insert_recent_repayments.sql
**插入近期还款记录**
- 7 条还款记录
- 还款方式：现金、微信、支付宝、银行转账
- 配合订单数据进行插入

### V6__insert_credit_evaluations.sql
**插入信用评估记录**
- 5 条信用评估记录
- 包含当前评估和历史评估
- 信用等级：A（优秀）、B（良好）、C（一般）

### V7__insert_reminder_rules.sql
**插入提醒规则配置**
- 6 条提醒规则
- 规则类型：逾期提醒、到期前提醒、季节性提醒
- 触发条件：天数、月份

### V8__insert_reminder_logs.sql
**插入提醒日志记录**
- 6 条提醒发送记录
- 包含已发送和待发送状态
- 通知渠道：APP、短信、微信

## 🎯 数据特点

### 订单数据覆盖场景
1. ✅ **正常流程**：待审核 → 已审核 → 部分还款 → 已还清
2. ✅ **逾期场景**：已逾期订单（26 天）
3. ✅ **异常流程**：已拒绝、已取消
4. ✅ **季节性订单**：春季批量采购

### 农户信用状况
1. **王大山（ID=1）**
   - 信用评分：780（A 级）
   - 总额度：10,000 元
   - 已用额度：3,000 元
   - 信用记录：良好

2. **刘富贵（ID=2）**
   - 信用评分：650（B 级）
   - 总额度：5,000 元
   - 已用额度：3,480 元
   - 信用记录：有少量逾期

3. **李老三（ID=3）**
   - 信用评分：520（C 级）
   - 总额度：3,000 元
   - 已用额度：2,700 元
   - 信用记录：有逾期记录

## 📊 数据统计

执行所有脚本后，数据库将新增：
- 订单记录：10 条
- 还款记录：7 条
- 信用评估：5 条
- 提醒规则：6 条
- 提醒日志：6 条

## 🔧 使用方法

### 方式 1：使用 Flyway 自动执行（推荐）
1. 确保 application.yml 中配置了 Flyway
2. 启动后端项目
3. Flyway 会自动按版本号顺序执行所有脚本

### 方式 2：手动执行
```bash
# 连接到 MySQL 数据库
mysql -u root -p farming_credit

# 依次执行脚本
source V4__insert_recent_orders.sql
source V5__insert_recent_repayments.sql
source V6__insert_credit_evaluations.sql
source V7__insert_reminder_rules.sql
source V8__insert_reminder_logs.sql
```

## ⚠️ 注意事项

1. **执行顺序**：必须按照 V4 → V5 → V6 → V7 → V8 的顺序执行
2. **依赖关系**：
   - V5 依赖 V4 的订单数据
   - V6 需要农户数据已存在
   - V7 和 V8 需要店铺数据已存在
3. **数据清理**：如需重新执行，请先清理相关表数据

## 📝 清理 SQL（可选）

```sql
-- 清理测试数据（谨慎使用）
DELETE FROM reminder_log WHERE store_id = 1;
DELETE FROM reminder_rule WHERE store_id = 1;
DELETE FROM credit_evaluation WHERE store_id = 1;
DELETE FROM repayment WHERE store_id = 1;
DELETE FROM credit_order WHERE store_id = 1;
```

## 🎨 测试场景

### 店主端测试
1. 查看仪表盘统计数据
2. 管理农户信用额度
3. 审核赊销订单
4. 登记还款记录
5. 查看统计报表
6. 配置提醒规则

### 农户端测试
1. 查看欠款总览
2. 查看订单列表
3. 查看信用评估
4. 查看还款记录

### 管理员端测试
1. 查看平台统计数据
2. 管理用户和角色
3. 查看系统日志
