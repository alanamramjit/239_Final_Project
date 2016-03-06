setwd('/Users/shaghayegh/Courses/Winter16/CS239/FinalProject')
mydata <- read.csv('metrics.csv')
cor(mydata$bug_fix, mydata$classes)
cor(mydata$bug_fix, mydata$methods)
cor(mydata$bug_fix, mydata$ncss)

lr <- lm(bug_fix~classes, data=mydata)
plot(mydata$bug_fix, fitted(lr))