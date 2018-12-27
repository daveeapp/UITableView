## UITableView
    A widget for android develop whitch like the view UITableView in iOS.
    
### Release 1.0.1

**Fixed**
- **修复设置sectionFooterSeparatorEnable = false时仍然绘制cell的separator的bug**

**Change**
- 修改contentStartOffset / contentEndOffset为单独的ItemDecorator
- 修改当SeparatorHeight < 1 时，直接设置为0，使用 hairline mode(系统会使用最小的宽度来绘制)

### Release 1.0.2

**Change**

- **删除SimpleAnimatorListener类，使用Android提供的AnimatorAdapterListener**


### Release 1.0.3

- **增加TableViewDataSourceAdapter类**
- **SectionTitle增加设置margin/gravity方法**

TODO:
- **修改方法名：onCreateTableViewCell->onCreateTableViewCell**
- **修改selection style 为none时，没有背景颜色**




