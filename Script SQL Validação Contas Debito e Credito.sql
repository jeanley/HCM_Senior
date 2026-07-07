select t2.deseve,t2.tipeve,t2.codclc,t3.codcon,t4.codcon,t1.* 
from r046ver t1
left join r008evc t2 on t1.codeve = t2.codeve and t2.codtab = 100
left join r174cnd t3 on t3.codccu = '01' and t3.numemp = 2 and t3.codclc = t2.codclc
left join r174cnd t4 on t4.codccu = '01' and t4.numemp = 2 and t4.codclc = t2.codclc+1000
where t1.numemp = 2 and codcal = 21329 and numcad = 20008349
