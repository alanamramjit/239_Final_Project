# setwd('/Users/shaghayegh/Courses/Winter16/CS239/FinalProject')
# install.packages("Hmisc",dependencies = TRUE)
library(DAAG)
library(Hmisc)
# mydata <- read.csv('metrics-2.csv')
mydata <- read.csv('5repo_all.csv')
# mydata$bug_fixes <- factor(mydata$bug_fixes)
# y <- mydata[2]
# x <- mydata[3:4]

rcorr(as.matrix(mydata[2:4]), type="spearman")
rcorr(as.matrix(mydata[2:4]), type="pearson")

lr_1 <- lm(bug_fixes~unique,data=mydata)
str(summary(lr_1))
rmse_1 <- sqrt(mean(lr_1$residuals^2))
dev.new()
par(mfrow=c(1,1))
plot(fitted(lr_1), ylab="Predicted (green) and Actual (red) bug fixes", xlab="Files", col="green")
points(mydata$bug_fixes, col="red")
#---------

predicted_1 <- fitted(lr_1)
rcorr(predicted_1, as.matrix(mydata[2]), type="spearman")

lr_2 <- lm(bug_fixes~unique+avg_len, data=mydata)
str(summary(lr_2))
rmse_2 <- sqrt(mean(lr_2$residuals^2))

dev.new()
par(mfrow=c(1,1))
plot(fitted(lr_1), ylab="Predicted (blue) and Actual (red) bug fixes", xlab="Files", col="blue")
points(mydata$bug_fixes, col="red")
#----------

predicted_2 <- fitted(lr_2)
rcorr(predicted_2, as.matrix(mydata[2]), type="spearman")


# cross_v5 <- CVlm(data=mydata, form.lm=lr, m=3)
# dev.new()
# par(mfrow=c(1,1))
# plot(fitted(lr), ylab="Predicted (green) and Actual (red) bug fixes", xlab="Files", col="green")

# regression <- glm(bug_fixes~unique, data=mydata, family="binomial")
# cross_v5_reg<- CVlm(data=mydata, form.lm=regression, m=3)
# dev.new()
# par(mfrow=c(1,1))
# plot(fitted(regression), ylab="Predicted (green) and Actual (red) bug fixes", xlab="Files", col="green")
# points(mydata$bug_fixes, col="red")

# res <- stack(data.frame(Observed = mydata$bug_fixes, Predicted=fitted(lr_1)))
# res <- cbind(res, x=rep(mydata$unique, 2))
# head(res)
# require("lattice")
# xyplot(values ~ x, data = res, group = ind, auto.key = TRUE)