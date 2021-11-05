# get data from file
# x <- read.table("toto.txt", header = TRUE)

# or manually here
nb <- c(100, 540, 320, 126)

# generate random data as if it comes from a file
nbauth <- 15 # nb auteurs, then generate names
nom <- paste("author", 1:nbauth, sep="") # author1,...author_nbauth
nb <- sample(nb, size = nbauth, replace = TRUE)
nb <- nb + sample(seq(-10,10,5), size = nbauth, replace = TRUE) # add some randomness
x <- data.frame(nom, nb)
x


pct <- 100*nb/sum(nb) # normalize to empirical probabilities (%)
# build colors from %
pcgood <- 10 # level qualifying what "good" means
cl <- (pct < pcgood)*2 # red
cl[cl == 0] <- 3   # green


# Do the plot
clab <- 1.4    # label size, 1 = default size
cnames <- 0.8  # bar names size, adapt to nb of bars
lgdbon <- paste("bon (>", pcgood,"%)")
barplot(pct, names.arg = nom, main="Commits par auteurs",
        ylim = c(0, min(c(100, max(pct)+10))),
        xlab="auteurs", ylab="% de commits" , 
        cex.names = cnames, cex.lab=clab, col=cl)
legend("topleft", legend = c(lgdbon,"mauvais"), fill=3:2,
       bg = "antiquewhite")
abline(h = pcgood, lty=2, col=8) # visualize "good" level...
#

# output plot to a file in png; see ?pdf or ?png
pdf(file = "mybarplot.pdf", width=7, height=5) # 1920/1080 full HD png
clab <- 1.4    # label size, 1 = default size
cnames <- 1  # bar names size, adapt to nb of bars
lgdbon <- paste("bon (>", pcgood,"%)")

barplot(pct, names.arg = nom, main="Commits par auteurs",
        ylim = c(0, min(c(100, max(pct)+10))),
        xlab="auteurs", ylab="% de commits" , 
        cex.names = cnames, cex.lab=clab, col=cl)
legend("topleft", legend = c(lgdbon,"mauvais"), fill=3:2,
       bg = "antiquewhite")
abline(h = pcgood, lty=2, col=8) # visualize "good" level...
dev.off() # close file
########
# pdf width=7, height=5

