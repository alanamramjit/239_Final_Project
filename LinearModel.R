setwd('/Users/shaghayegh/Courses/Winter16/CS239/FinalProject')
# install.packages("Hmisc",dependencies = TRUE)
library(DAAG)
library(Hmisc)
mydata <- read.csv('new-metrics.csv')
# mydata$bug_fixes <- factor(mydata$bug_fixes)
y <- mydata[2]
x <- mydata[3:9]

rcorr(as.matrix(mydata[2:9]), type="spearman")
rcorr(as.matrix(mydata[2:9]), type="pearson")

lr_1 <- lm(bug_fixes~id_unique, data=mydata)
summary(lr_1)$adj.r.squared #0.445
summary(lr_1)$r.squared

lr_2 <- lm(bug_fixes~id_unique+id_avg, data=mydata)
summary(lr_2)$adj.r.squared #---- adj. R^2 = 0.468
summary(lr_2)$r.squared
rmse_2 <- sqrt(mean(lr_2$residuals^2))

lr_3 <- lm(bug_fixes~id_unique+id_avg+method_avg, data=mydata)
summary(lr_3)$adj.r.squared #---- adj. R^2 = 0.454

lr_4 <- lm(bug_fixes~id_unique+method_avg, data=mydata)
summary(lr_4)$adj.r.squared #---- adj. R^2 = 0.455 drop id_avg

lr_5 <- lm(bug_fixes~id_unique+method_avg+method_unique, data=mydata)
summary(lr_5)$adj.r.squared #---- adj. R^2 = 0.634

lr_6 <- lm(bug_fixes~id_unique+method_avg+method_unique+token_avg, data=mydata)
summary(lr_6)$adj.r.squared #---- adj. R^2 = 0.628

lr_7 <- lm(bug_fixes~id_unique+method_avg+method_unique+token_avg+token_unique, data=mydata)
summary(lr_7)$adj.r.squared #---- adj. R^2 = 0.619

lr_8 <- lm(bug_fixes~id_unique+method_avg+method_unique+token_unique, data=mydata)
summary(lr_8)$adj.r.squared #---- adj. R^2 = 0.626 #---- drop token_avg

lr_9 <- lm(bug_fixes~id_unique+method_avg+method_unique+token_avg+token_unique+file_size, data=mydata)
summary(lr_9)$adj.r.squared #---- adj. R^2 = 0.780

lr_10 <- lm(bug_fixes~id_unique+method_avg+method_unique+token_avg+file_size, data=mydata)
summary(lr_10)$adj.r.squared #---- adj. R^2 = 0.7889 #----drop token_unique
rmse_10 <- sqrt(mean(lr_10$residuals^2))

#---------------------------------
dev.new()
par(mfrow=c(1,1))
plot(fitted(lr_9), ylab="Predicted (green) and Actual (red) bug fixes", xlab="Files", col="green")
points(mydata$bug_fixes, col="red")
