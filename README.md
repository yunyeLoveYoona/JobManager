# JobManager
任务管理 用来存储和管理当前不执行但可能以后会执行 或者触发某些条件后执行的任务比如广播


Android开发中经常需要注册广播，并且附带有接收到广播后的任务，JobManager就是为了统一管理广播的注册以及任务，减少重复代码，简化逻辑。


还有一些可能需要特殊场景触发的任务或者多个场景共同需要的任务，都可以放入JobManager中管理。
