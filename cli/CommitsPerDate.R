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



# output plot to a file in png; see ?pdf or ?png
pdf(file = "CommitsPerDate.pdf", width=7, height=5) # 1920/1080 full HD png
plot(dt, nb, type="b", pch=20, xlab = "Date", ylab="Commits")
  
dev.off() # close file

########
# pdf width=7, height=5

