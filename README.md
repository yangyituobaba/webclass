# 前端

| 接口             | 请求方式 | 是否需认证 | 关键字段                    | 断言逻辑           | 预期响应码 | 说明     |
| -------------- | ---- | ----- | ----------------------- | -------------- | ----- | ------ |
| /user/register | POST | ❌     | username/password/phone | msg 包含 "注册成功"  | 200   | 测试正常注册 |
| /user/register | POST | ❌     | 重复用户名                   | msg 包含 "已存在"   | 200   | 测试注册失败 |
| /user/login    | POST | ❌     | 正确用户名密码                 | data.token 不为空 | 200   | 登录成功   |
| /user/login    | POST | ❌     | 错误密码                    | msg="用户名或密码错误" | 200   | 登录失败   |
| /user/info     | POST | ✅     | header: Authorization   | 返回用户信息         | 200   | 获取当前用户 |
| /user/info     | POST | ✅     | 无Token                  | msg="用户未登录"    | 200   | 失败逻辑测试 |
| /user/all      | GET  | ✅     | 管理员身份Token              | 返回用户列表         | 200   | 获取所有用户 |
| /user/update   | PUT  | ✅     | 正确原密码与新数据               | msg="更新成功"     | 200   | 成功修改   |
| /user/update   | PUT  | ✅     | 原密码错误                   | msg="当前密码错误"   | 200   | 更新失败   |

