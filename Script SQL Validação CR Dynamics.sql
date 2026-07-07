/* identifica se tem alguém com CRs que não é do Dynamics */
SELECT t1.numemp,t1.codfil,t1.tipcol,t1.numcad,t1.codclc,t1.cplhis,t1.codrat,t1.codccu,t1.condeb,t1.concre,t1.vallan,t2.ccutxt
FROM R048CTB t1 
left join r018ccu t2 on t1.numemp = t2.numemp and t1.codrat = t2.codrat
WHERE t1.NUMEMP = 2 AND DATLAN = '2026-01-31' and t2.ccutxt <> 'dynamics'

with LISTA_COLABORADORES_SOME AS
(
SELECT DISTINCT
        A.NUMEMP,
        A.TIPCOL,
        A.NUMCAD,
        H.SITAFA,
        H.DATAFA
    FROM R034FUN A WITH (NOLOCK)
    LEFT JOIN R038AFA H
        ON H.NUMEMP = A.NUMEMP
       AND H.TIPCOL = A.TIPCOL
       AND H.NUMCAD = A.NUMCAD
       AND H.SITAFA IN (7,18)
    LEFT JOIN R018CCU J
        ON J.NUMEMP = A.NUMEMP
       AND J.CODCCU = A.CODCCU
    WHERE A.NUMEMP = 2
      AND A.TIPCOL = 1
      AND (
            A.SITAFA NOT IN (7,18)
            OR (A.SITAFA IN (7,18) AND H.DATAFA >= '2026-01-01')
          )
)
/* monitora o cadastro do colaborador o histórico de CR e o posto */ 
SELECT T1.*,z.nomfun,z.datadm,z.postra, z.estpos ,T2.*, z.codccu , T5.ccutxt , T7.postra, T7.codccu, T8.ccutxt
FROM LISTA_COLABORADORES_SOME T1
OUTER APPLY (              
    SELECT TOP (1) HCA.NUMEMP, HCA.NUMCAD, HCA.TIPCOL, HCA.codccu  , HCA.datalt            
    FROM R038HCC HCA WITH (NOLOCK)              
    WHERE HCA.NUMEMP = T1.NUMEMP              
      AND HCA.TIPCOL = T1.TIPCOL              
      AND HCA.NUMCAD = T1.NUMCAD
    ORDER BY HCA.DATALT DESC              
) T2
LEFT JOIN R038HCC T3 ON T2.NUMEMP = T3.NUMEMP AND T2.TIPCOL = T3.TIPCOL AND T2.NUMCAD = T3.NUMCAD AND T2.datalt = T3.datalt
LEFT JOIN R034FUN Z ON Z.NUMEMP = T1.NUMEMP AND Z.TIPCOL = T1.TIPCOL AND Z.NUMCAD = T1.NUMCAD
LEFT JOIN r018ccu T5 ON T5.NUMEMP = T3.NUMEMP AND T5.codccu = T3.codccu
 
OUTER APPLY (              
    SELECT TOP (1) CAR.NUMEMP, CAR.estpos,CAR.postra, CAR.TIPCOL, CAR.codccu  , CAR.datfim            
    FROM r017car CAR WITH (NOLOCK)              
    WHERE CAR.NUMEMP = Z.NUMEMP              
      AND CAR.TIPCOL = Z.TIPCOL              
      AND CAR.postra = Z.postra
    ORDER BY CAR.datfim DESC              
) T6
 
LEFT JOIN R017CAR T7 ON T7.NUMEMP = T6.NUMEMP AND T7.postra = T6.postra AND T7.estpos = T6.estpos AND T7.datfim = T6.datfim
LEFT JOIN R018CCU T8 ON T8.NUMEMP = T7.NUMEMP AND T8.codccu = T7.codccu
 
--where t5.ccutxt <> 'DYNAMICS'
ORDER BY 6
