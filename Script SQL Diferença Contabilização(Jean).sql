with Dados as(
select  
 
    A.codfil  Filial,   
    A.numemp Empresa,  
    FORMAT(a.datlan, 'yyyy') AnoRef,  
    FORMAT(a.datlan, 'MM') MesRef,  
     (select sum(a2.vallan)  
         from r048ctb a2,  
              r048clc b2  
        where a2.tabeve = b2.tabeve  
          and a2.codclc = b2.codclc  
          and a.numemp = a2.numemp  
          and a.filctb = a2.filctb  
          and a.codcal = a2.codcal  
          and a.datlan = a2.datlan  
		  and a.numcad = a2.numcad
          and a2.debcre = 'D') TotalDebito,  
    (select sum(a2.vallan * -1)  
         from r048ctb a2,  
              r048clc b2  
        where a2.tabeve = b2.tabeve  
          and a2.codclc = b2.codclc  
          and a.numemp = a2.numemp  
          and a.filctb = a2.filctb  
          and a.codcal = a2.codcal  
          and a.datlan = a2.datlan  
		  and a.numcad = a2.numcad
          and a2.debcre = 'C') TotalCredito,  
    FORMAT(a.datlan, 'dd') DiaRef ,
	a.numcad,
	c.nomfun,
	C.tipcon
from r048ctb a,  
     r048clc b,
	 R034FUN c
where  a.tabeve = b.tabeve  
  and a.codclc = b.codclc  
  AND A.numemp = 1
  and c.numcad = a.numcad
  and c.tipcol = a.tipcol
  and a.conger = '0'  
  and FORMAT(a.datlan, 'yyyy') = 2025
  and FORMAT(a.datlan, 'MM') = 10
group by  
 
    a.filctb,  
    a.numemp, 
	A.codfil ,
    a.datlan,  
    a.codcal,
	a.numcad,
	c.nomfun,
	C.tipcon
	)
 
SELECT *,TotalCredito - (TotalDebito*-1) AS Diferenca FROM DADOS WHERE TotalCredito <> (TotalDebito*-1) --and tipcon <> 5 --and filial = 1002
order by EMPRESA,FILIAL,nomfun
