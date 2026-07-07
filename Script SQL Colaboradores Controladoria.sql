select getdate() as dataReferencia,    
       a.numemp as codigoEmpresa,    
a.numcad as Matricula,      
upper(a.nomfun) as NomeCompleto,      
--a.datnas as dataNascimento,    
a.codcar as codigoCargo,    
upper(c.titcar) as CargoFuncao,  
c.codcb2 as CBO,
a.datadm as DataAdmissao,
a.deffis as Deficiencia,
  a.codccu as codCentroCusto,    
  j.nomccu as nomeCentroCusto,    
  upper(d.dessit) as Status,    
  case     
   when a.sitafa in (7,18) then y.desdem    
   else null    
  end as motivoDesligamento,    
  case      
   when h.sitafa is not null then h.datafa      
   else null      
  end as dataDesligamento,    
  case    
   when a.sitafa not in (1,7,18) then hh.datafa    
   else null    
  end as dataAfastamento,    
  a.codfil as Unidade,    
  i.nomfil as nomeUnidade,    
  q.valkey as regional,    
  b.emapar as emailParticular,    
  b.emacom as emailCorporativo,    
  g.matriculaGestor,    
  g.nomeGestor,      
  g.cargoGestor,    
  a.tipsex as Genero, 
  l.valkey as EstadoCivil,
  m.desnac as Nacionalidade,
  a.postra as postoColaborador,  
  --pos.despos as descrPostoColaborador,
  --z.numloc as numloc,
  --z.nomloc as local,
  n.valkey as RacaCor,
  p.nomsin as nomeSindicato,    
  o.codsin as codigoSindicato,    
  p.mesdis as dataBaseSindicato,    
  r.codvin as codigoVinculo,    
  s.desvin as nomeVinculo,
  t.desgra as Escolaridade,
  u.hormes / 60 as CargaHoraia,
   case    
   when (a.coddef is null or a.coddef = 0) then null    
   else upper(v.desdef)    
  end as TipoDeficiencia  
  --z.numloc, Z.nomloc, AA.codloc
from r034fun (nolock) a       
  inner join r034cpl b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad      
  left join r024car c on c.estcar = a.estcar and c.codcar = a.codcar      
  left join r010sit d on d.codsit = a.sitafa      
  left join r074bai e on e.codbai = b.codbai and e.codcid = b.codcid      
  left join r074cid f on f.codcid = b.codcid      
  left join VW_BI_HieCol g on g.codigoEmpresaColaborador = a.numemp and g.tipoColaborador = a.tipcol and g.matriculaColaborador = a.numcad      
  left join r038afa h on h.numemp = a.numemp and h.tipcol = a.tipcol and h.numcad = a.numcad and h.sitafa in (7,18)    
  left join r038afa hh on hh.numemp = a.numemp and hh.tipcol = a.tipcol and hh.numcad = a.numcad and hh.sitafa = a.sitafa    
       and hh.datafa = (select max(datafa) from r038afa afa where afa.numemp = a.numemp and afa.tipcol = a.tipcol and afa.numcad     
= a.numcad and afa.sitafa = a.sitafa)    
  left join r030fil i on i.codfil = a.codfil and i.numemp = a.numemp      
  left join r018ccu j on j.numemp = a.numemp and j.codccu = a.codccu    
  left join r074cid k on k.codcid = b.ccinas    
  left join r996lsf l on l.keynam = a.estciv and l.lstnam = 'lEstciv'    
  left join r023nac m on m.codnac = a.codnac    
  left join r996lsf n on n.keynam = a.raccor and n.lstnam = 'lraccor'    
  left join r038hsi o on o.numemp = a.numemp and o.tipcol = a.tipcol and o.numcad = a.numcad    
                      and o.datalt = (select max(datalt) from r038hsi hsi where hsi.numemp = a.numemp and hsi.tipcol = a.tipcol and hsi.numcad = a.numcad)    
  left join r014sin p on p.codsin = o.codsin    
  left join r996lsf q on q.keynam = i.usu_region and q.lstnam = 'usu_lRegion'    
  left join r038hvi (nolock) r on r.numemp = a.numemp and r.tipcol = a.tipcol and r.numcad = a.numcad    
                            and r.datalt = (select max(datalt) from r038hvi hsi where hsi.numemp = a.numemp and hsi.tipcol = a.tipcol and hsi.numcad = a.numcad and     
hsi.datalt <= getdate())    
        left join r022vin s on s.codvin = r.codvin    
  left join r022gra t on t.grains = a.grains    
  left join r006esc u on u.codesc = a.codesc    
  left join r022def v on v.coddef = a.coddef    
  left join r042rcm x on x.numemp = a.numemp and x.tipcol = a.tipcol and x.numcad = a.numcad    
  left join r042cau y on y.caudem = x.caudem    
  left join r016orn z on z.taborg = a.taborg and z.numloc = a.numloc
  left join r016hie aa on aa.taborg = z.taborg AND AA.numloc = Z.numloc
  left join r017pos pos on pos.estpos =a.estpos and pos.postra = a.postra and pos.datext = convert(date,'1900-12-31 00:00:00.000')
where 1 = 1      
and  a.tipcol = 1      
and  a.numemp < 5      
and  ( (a.sitafa not in (7,18) ) or ( a.sitafa in(7,18) and h.datafa > getDate() - 730) )      
