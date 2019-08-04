# CodingStatisticsManager
基于Intellij IDEA的 coding 统计插件。我想管理统计一下我打代码的数据，包括分析打代码的时间段
、每天码代码的量(行)，随便统计一下我想的项目的代码行数什么的，于是就有了这个项目。  
插件的数据是持久化到数据的，使用若要使用本插件，首先要配置一下数据库。  
tip: 这个项目是参照 [coding-count](https://github.com/maff91/Coding-Counter) 想到的，代码中也参照了其中的代码。


# 目标功能  
### 1、提供一个Application级的Component, 为所有的项目count下面的参数：

- 所有键入的字符;  
- 删除的字符(使用Backspace/Del）;  
- 剪切的字符;
- 粘贴的字符;
- 所有删除的字符(cut, del, backspace);
- 所有的增加的字符(键入+剪贴);
- 所有的代码行数(包括删除的);
- 增加的代码行数(包括空行);
- 有效的代码行数;

#### 统计的数据分为三个周期：

- 天;
- 周;
- 月;

### 2、提供一个Project级的Component, 对项目的一些数据进行管理，包括：

- 项目创建的时间;
- 项目所有的文件及其代码行数；
- 项目总的代码行数;

# 目前实行的功能  

### Application级的Component：

- 所有键入的字符;  
- 删除的字符(使用Backspace/Del）;  
- 剪切的字符;
- 粘贴的字符;
- 所有删除的字符(cut, del, backspace);
- 所有的增加的字符(键入+剪贴);

# 如何使用

- 配置数据库
- 安装插件

# Licence

[MPL](https://opensource.org/licenses/MPL-2.0)