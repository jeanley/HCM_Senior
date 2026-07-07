SELECT a.NumEmp as CódigoEmpresa, a.TipCol as Tipo, a.NumCad as Matricula, a.NumLoc as CódigoLocal, b.desred as PostoTrabalho, e.nomloc as Departamento, a.NomFun as Nome, a.CodCar as CódigoCargo, c.titcar as Cargo, a.DatAdm as Admissão, d.emacom as EmailMaristaBrasil 
FROM R034FUN a
left join R017POS b (nolock) on b.estpos = a.estpos and b.postra = a.postra
left join R024CAR c (nolock) on c.estcar = a.estcar and c.codcar = a.codcar 
left join R034CPL d (nolock) on d.numemp = a.numemp and d.tipcol = a.tipcol and d.numcad = a.numcad
left join R016ORN e (nolock) on e.taborg = a.taborg and e.numloc = a.numloc
WHERE a.sitafa <> 7 and (b.postra = '3001.027' or b.postra = '3001.075' or b.postra = '3001.081' or b.postra = '2056.078' or 
b.postra = '1053.182' or b.postra = '3001.079' or b.postra = '1053.111' or b.postra = '1053.280' or b.postra = '3001.222' or 
b.postra = '3001.230' or b.postra = '3001.231' or b.postra = '3001.080' or b.postra = '3001.190' or b.postra = '2056.005' or 
b.postra = '1053.268' or b.postra = '3001.213' or b.postra = '3001.214' or b.postra = '3001.189' or b.postra = '1053.112' or 
b.postra = '1053.107' or b.postra = '3001.076' or b.postra = '2056.024' or b.postra = '1053.152' or b.postra = '3001.077' or 
b.postra = '1053.108' or b.postra = '1053.113' or b.postra = '1053.110' or b.postra = '2056.074' or b.postra = '2056.030' or 
b.postra = '3001.204' or b.postra = '2056.145' or b.postra = '1001.020' or b.postra = '2056.069' or b.postra = '2056.043' or 
b.postra = '2056.023' or b.postra = '2056.073' or b.postra = '2056.082' or b.postra = '2056.093' or b.postra = '2056.128' or 
b.postra = '2056.123' or b.postra = '2056.124' or b.postra = '1053.195' or b.postra = '2056.045' or b.postra = '2056.044' or 
b.postra = '2056.127' or b.postra = '2056.125' or b.postra = '1053.281' or b.postra = '1053.282' or b.postra = '3001.098' or 
b.postra = '2056.126')
