# get data from file
# column 1 is labels (row names)
w <- getwd()
w

setwd(".visulogRTempFiles")
#z <- paste(w,".visulogRTempFiles",sep="/")
x <- read.table("commitsPerDate.txt", header = FALSE, row.names = 1)
colnames(x) <- "nb"

x <- data.frame(Date = as.character(row.names(x)), x)
attach(x)

# handling Date 
Sys.getlocale("LC_TIME")
dt <- as.Date(Date) # convert to Date

## SETTINGS:


setwd("../")
# output plot to a file in png; see ?pdf or ?png
pdf(file = "CommitsPerDate.pdf", width=9, height=7) # 1920/1080 full HD png
plot(dt, nb, type="l", pch=20, xaxt = "n", xlab = "Date", ylab="Commits")
# axis.Date(1, at = dt, format = "%b %y")  
axis.Date(1, at = dt, format = "%d %b %y", cex.axis=0.7)  
# axis.Date(1, at = dt, format = "%d %b %y", las=2)  
dev.off() # close file

########
# pdf width=7, height=5

