# get data from file
# column 1 is labels (row names)
w <- getwd() 
#setwd("../")
w
#setwd("/Users/cyprien/visulog/cli/.visulogRTempFiles")
setwd(".visulogRTempFiles")
#z <- paste(w,".visulogRTempFiles",sep="/")
x <- read.table("commitsPerDate.txt", header = FALSE)

colnames(x) <- c("Date", "nb")

#x <- data.frame(Date = as.character(row.names(x)), x)

attach(x)

# handling Date with local system time


Sys.getlocale("LC_TIME")
dt <- as.Date(Date, "%Y-%m-%d") # convert to Date

setwd("../")
setwd(".graphs")
# output plots to files in pdf
ymt <- substr(Date, 1, 7)
ind <- table(ymt)
nl <- length(ind) # nb of months in data
start <- 1
for (i in 1:nl) { # for each year-month
  dtfig <- dt[start:(start + ind[i]-1)]
  nbfig <- nb[start:(start + ind[i]-1)]
  fname <- paste("CommitsPerDate_",i,".pdf", sep="")
  pdf(file = fname, width=9, height=7) # 1920/1080 full HD png
  plot(dtfig, nbfig, type="b", pch = 20, xaxt = "n", xlab = "Jour du mois", ylab="Commits")
  axis.Date(1, at = dtfig, format = "%d %b", cex.axis=1)  # set %B for full months
  title(paste("Periode:", names(ind[i])))
  dev.off() # close file
  start <- start + ind[i]
}




