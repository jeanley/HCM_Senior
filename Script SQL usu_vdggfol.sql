select	e.valkey as calculo,
		c.fimcmp as dataReferencia,
		a.numemp as empresa,
		b.codfil as codFilial,
		f.numcgc as cnpjFilial,
		upper(f.nomfil) as filial,
		a.numcad as contrato,
		b.nomfun as funcionario,
		b.codccu as centroCusto,
		g.nomccu as descricaoCentroCusto,
		d.tipeve as tipoEvento,
		a.codeve as evento,
		d.deseve as nomeEvento,
		00 as horas,
		a.refeve as referencia,
		a.valeve as valor,
		i.numcgc as cnpjSindicato,
		i.nomsin,
		j.codcar as codCargo,
		k.titcar as nomeCargo,
		l.dessit as situacao,
		case
			when m.tiplan = 'D' then cast(d.codclc as varchar) + ' - ' + m.nomcon
			else null
		end as CodContaDebito,	
		case
			when m.tiplan = 'C' then cast(d.codclc as varchar) + ' - ' + m.nomcon
			else null
		end as CodContaCredito,
		'00.00' as EstruturaPlano_Debito,
		'00.00' as EstruturaPlano_Credito
from	r046ver (nolock) a
		inner join r034fun (nolock) b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad
		inner join r044cal (nolock) c on c.numemp = a.numemp and c.codcal = a.codcal
		inner join r008evc (nolock) d on d.codtab = a.tabeve and d.codeve = a.codeve
		inner join r996lsf (nolock) e on e.keynam = c.tipcal and e.lstnam = 'lTipCal'
		inner join r030fil (nolock) f on f.numemp = b.numemp and f.codfil = b.codfil
		inner join r018ccu (nolock) g on g.numemp = b.numemp and g.codccu = b.codccu
		left join r038hsi (nolock) h on h.numemp = a.numemp and h.tipcol = a.tipcol and h.numcad = a.numcad
		                            and h.datalt = (select max(datalt) from r038hsi hsi where hsi.numemp = a.numemp and hsi.tipcol = a.tipcol and hsi.numcad = a.numcad and hsi.datalt < c.fimcmp)
		left join r014sin i on i.codsin = h.codsin
		left join r038hca (nolock) j on j.numemp = a.numemp and j.tipcol = a.tipcol and j.numcad = a.numcad
		                            and j.datalt = (select max(datalt) from r038hca hca where hca.numemp = a.numemp and hca.tipcol = a.tipcol and hca.numcad = a.numcad and hca.datalt < c.fimcmp)
		left join r024car (nolock) k on k.estcar = j.estcar and k.codcar = j.codcar
		inner join r010sit (nolock) l on l.codsit = b.sitafa
		left join r048clc (nolock) m on m.codclc = d.codclc
where	1 = 1
and		c.fimcmp > getdate() - 90


--73981 registros
