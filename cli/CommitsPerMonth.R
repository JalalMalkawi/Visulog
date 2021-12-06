# get data from file
# column 1 is labels (row names)
w <- getwd()
w
#setwd("/Users/cyprien/Desktop/testRgene")
setwd(".visulogRTempFiles")

#z <- paste(w,".visulogRTempFiles",sep="/")
x <- read.table("commitsPerMonth.txt", header = FALSE)
colnames(x) <- c("nb", "auteurs")
attach(x)

## SETTINGS:
prob <- FALSE # set to TRUE if barplot(%) desired
tofile <- TRUE # set to FALSE to print in RStudio




pct <- 100*nb/sum(nb) # normalize to empirical probabilities (%)
# build colors from %
pcgood <- 10 # level qualifying what "good" means
cl <- (pct < pcgood)*2 # red
cl[cl == 0] <- 3   # green

# Do the plot
clab <- 1.4    # label size, 1 = default size
cnames <- 0.1  # bar names size, adapt to nb of bars
lgdbon <- paste("bon (>", pcgood,"%)")
setwd("../")
setwd(".graphs")

if (! tofile) if (prob) {
  barplot(pct, names.arg = auteurs, main="Commits par auteurs (%)",
        ylim = c(0, min(c(100, max(pct)+10))),
        xlab="auteurs", ylab="% de commits" , 
        cex.names = cnames, cex.lab=clab, col=cl, las = 2)
  legend("topleft", legend = c(lgdbon,"mauvais"), fill=3:2,
       bg = "antiquewhite")
  # visualize "good" level...
  abline(h = pcgood, lty=2, col=8)
} else {
  barplot(nb, names.arg = auteurs, main="Commits par auteurs",
          xlab="auteurs", ylab="nombre de commits", 
          cex.names = cnames, cex.lab=clab, las = 2)}

# output plot to a file in png; see ?pdf or ?png
if (tofile) {
  pdf(file = "CommitsPerMonth.pdf", width=9, height=7) # 1920/1080 full HD png
  clab <- 1.4    # label size, 1 = default size
  cnames <- 0.7  # bar names size, adapt to nb of bars
  
  if (prob) {
    barplot(pct, names.arg = auteurs, main="Commits par auteurs (%)",
            ylim = c(0, min(c(100, max(pct)+10))),
            xlab="auteurs", ylab="% de commits" , 
            cex.names = cnames, cex.lab=clab, col=cl)
    legend("topleft", legend = c(lgdbon,"mauvais"), fill=3:2,
           bg = "antiquewhite")
    # visualize "good" level...
    abline(h = pcgood, lty=2, col=8)
  } else {
    barplot(nb, names.arg = auteurs, main="Commits per Month",
            xlab="", ylab="number of commits", 
            cex.names = cnames, cex.lab=clab)}
  
  dev.off() # close file
}
########
# pdf width=7, height=5

