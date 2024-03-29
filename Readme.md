# cm-bs-helper

一个用 kotlin multiplatform 实现的类 beatsaver 游戏的谱面管理工具。
## 功能说明
目前实现的功能包括
1. 本地曲包的基本管理
2. BeatSaver 谱面浏览，可以下载单个/批量曲包，playlist
3. 曲包的分享/导入（不完全可用）

目前的设想的主要使用场景就是：
1. 戴上头显后，看看最近的新谱面，挑一挑看描述还行的，下载然后就可以去某游戏愉快的开房了。
2. 或者是在 beatsaver 上挑一些歌生成一个歌单，戴上头显直接下载就可以了，免除连线的烦恼。

虽然通过第三方集成了谱面预览功能([ArcViewer](https://github.com/AllPoland/ArcViewer))，但限于webview的性能限制，在头显端使用体验并不好，需要关闭一些 feature。降低渲染精度。

## 错误处理

因为我写的各种各样的花式bug，运行时可能产生预料之外的行为。
一般的报错会进行提示，但有的时候提示并没有用。这种情况下，可以清除数据重试。

也可以查看日志文件，可能会有帮助。
- Android 位于 `Android/data/io.ktlab.bshelper/files/logs`
- Desktop 位于 `${YOUR_HOME_DIR}/.bs-helper/logs`

当然，也可能碰到会无法处理的错误，如果你愿意，可以通过邮箱/GitHub 联系我。 在时间充裕的情况下，我应该会跟进处理的。


## 关于本项目
本应用是一个Toy项目，其源于某款好玩的 BeatSaber like VR 游戏，但不巧它有着垃圾的谱面管理机制。 遂心生一计，彼可取而代之（手动狗头）。

但这个过程是艰难的。作为一个伪前端，实际上的 CRUD Guy，对 Android 开发这一块的涉猎仅限于大学一学期的作业和网络吹水。
想法有了几个月，然后花了亿些时间用断断续续断断断断断断续续的方式堆来堆这个应用。堆成了现在这样的💩山。

在作出第一版 demo 的时候，已经是几个月前了，不得不说纯 Android 开发的体验是飞一般的爽，毕竟生态很成熟，几天就搞定了。

但网上冲浪过程中，又看到了 compose multiplatform 这个看起来 dio 爆了的 UI 框架，它是 JetBrains 出的，它用 kotlin，它一跨多诶，🆒！（什么，RN，Flutter？🥹真不熟）。

于是开始了转 compose multiplatform 的过程。

> 不得不说，在这里我想大声宣布四个字，WSSB！我还是图样图森破了。
> 
> 痛，太痛了！compose multiplatform 虽然依托于 kotlin multiplatform。
> 
> 但是在 desktop 端的 platform specific API 的调用能力约等于 0，现成可用的库不多，想用就得自己封装（做不到.jpg）。
> 
> 而且一些功能库真的找不到啊，kotlin multiplatform 的生态太小了，当然主要还是因为我菜。
> 
> 为了找一个能用的库，我就像在互联网中💩里掏金。GPT 见了都得管我叫声掏💩侠。
> 
> 不过好在还是有一些的，至少现在把一些基础功能实现了。
> 
> 好了，卖惨到此为止，只为博君一乐。

实际上当时也考虑了 BS 的曲包适配才去踩了 compose multiplatform 这个坑，这是前期调研上出现的严重错误。
实际使用过后， BS Mod提供的曲包能力已经很不错了，后来也发现了像[BS-Manager](https://github.com/Zagrios/bs-manager) 这样优秀的管理工具。


## 这个玩具的未来
作为一个android 新手，kotlin 新手，kotlin multiplatform 新手，compose multiplatform 新手。

这么多 debuff 叠加在一起造出这样的玩具，效果是可想而知的。它很垃圾。

它的使用场景也很狭窄。用户数能不能突破 10 位也是个问题。

~~它的 web 功能也很受限，跟网络条件强相关，不巧的是 Beatsaver 的 API 在国内的网络条件下会出问题。~~

~~而且某游戏的官方应该也在完善类似的功能。 所以它的未来大概率是不可期的，还能有几次更新也不知道。~~

~~所以大概是没有未来的😢。~~

~~但如果精力允许，且有稳定的小部分用户，我想我会积极进行维护的。~~

说不定呢

## TODO
- [x] feedback page
- [x] 多文件夹支持
- [x] 类 beatsaber 游戏适配（如功夫节奏/切方块等）
- [x] 伪备份（用于卸载用途，防止卸载后曲包丢失，重装游戏后应该第一时间进行恢复）
- [ ] 优化下载机制
- [ ] 优化分享/导入机制
- [ ] 曲包同步
- [ ] 优化 UI
  - [x] 重构 beatmap
  - [ ] 重构 playlist
- [ ] more

## 大饼时间
当然，我大可以畅想一下，可能会加的功能(现在是幻想（画饼）时间.jpg)
- 曲包分类，通过规则配置，对某些曲包进行划分，比如按速度划分，按 Tag 划分
- 远程管理，戴上头显管理曲包还是太繁琐，不如直接在手机/电脑上管理头显上的曲包。
- 为类似的游戏曲包导入做适配，比如 xxx，xxx

## contribute
如果你有空闲时间，正好专业对口，同时也愿意来一起掏这个💩山。欢迎 PR。不合理的地方也请各位大神指正。

cm-bs-helper is an open source project and built on the top of open-source projects. 

Thanks for [JetBrains](https://www.jetbrains.com/?from=cm-bs-helper) for the wonderful IDE.

![JetBrains Logo (Main) logo](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)
