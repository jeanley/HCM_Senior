package custom.senior.apuracao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.senior.ContextoGeralRH;
import com.senior.dataset.ICursor;
import com.senior.dataset.MappedParamProvider;
import com.senior.dataset.OrderDirection;
import com.senior.dbc.DBCenter;
import com.senior.dbc.intfs.IDBCenter;
import com.senior.dbc.intfs.IResultSet;
import com.senior.dbc.intfs.ISession;
import com.senior.entity.IContaBH;
import com.senior.entitysession.EntitySessionProvider;
import com.senior.entitysession.IEntitySession;
import com.senior.rh.entities.readonly.IR004HOR;
import com.senior.rh.entities.readonly.IR006ESC;
import com.senior.rh.entities.readonly.IR010SIT;
import com.senior.rh.entities.readonly.IR010TOB;
import com.senior.rh.entities.readonly.IR044CAL;
import com.senior.rh.entities.readonly.IR070ACC;
import com.senior.rh.ponto.apuracao.calculo.TipoIntervalo;
import com.senior.rh.ponto.colaborador.Compensacao;
import com.senior.rh.ponto.colaborador.Feriado;
import com.senior.rh.ponto.colaborador.HistoricoAfastamento;
import com.senior.rh.ponto.horas.ISeparacaoHoras;
import com.senior.rh.ponto.marcacoes.MarcacaoRegra;
import com.senior.rule.Rule;

@Rule(description = "Regra_Apuracao")
public class RegraApuracao extends Apuracao {
	
	int TOTALIZADOR_HORAEXTRA = 2;
	int TOTALIZADOR_HORAFALTA = 1;
	int TOTALIZADOR_HORASTRAB = 3;

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
		ContextoApuracao CxApuracao = getContainer().getContextoApuracao();

		int iToEHor = 0;
		int iToFHor = 0;
		
		int iNumEmp = CxApuracao.getColaborador().getNumEmp();
		int iTipCol = CxApuracao.getColaborador().getTipCol();
		int iNumCad = CxApuracao.getColaborador().getNumCad();
		int iCodHor = CxApuracao.getHorario().getCodigo();           //Código Horário
		int iCodEsc = CxApuracao.getEscala().getCodigo();            //Código Escala
		int iTipCon = CxApuracao.getColaborador().getTipoContrato(); //Tipo de Contrato
		int iClaEsc = CxApuracao.getEscala().getClasse();            //Classe de Escala 
		
		//Horas Previstas no Dia
		ISeparacaoHoras separacaoHoras = CxApuracao.getHorasPrevistas(iCodHor);
		int iFatRed = separacaoHoras.getTotalHoras();
		
		//Quantidade de Marcações
		int iQtdMar = CxApuracao.getMarcacoesRealizadas(true).size();
		
		//Verificar campos Tolerância Diária
		if (iCodHor < 9996) {
			int ArrayCamposTotalDiario[] = BuscaCamposTolDiara(iCodHor);
			iToEHor= ArrayCamposTotalDiario[0] ;
			iToFHor = ArrayCamposTotalDiario[1] ;
		}		
		//Busca Situações Padrões para as Rotinas
		int ArrayCarregaSituacao[] = CarregaSituacaoes();
		int iSitTra = ArrayCarregaSituacao[0];
		int iAdiNot = ArrayCarregaSituacao[1];
		int iSitNid = ArrayCarregaSituacao[2];
		int iNorAnt = ArrayCarregaSituacao[3];
		int iSepAnt = ArrayCarregaSituacao[4];
		int iNorFol = ArrayCarregaSituacao[5];
		int iSepFol = ArrayCarregaSituacao[6];
		int iNorCom = ArrayCarregaSituacao[7];
		int iSepCom = ArrayCarregaSituacao[8];
		int iNorDes = ArrayCarregaSituacao[9];
		int iNorFer = ArrayCarregaSituacao[10];
		
		int iExt10h = ArrayCarregaSituacao[11];
		int iSep10h = ArrayCarregaSituacao[12];
		int iExtAci = ArrayCarregaSituacao[13];
		int iExt10hCom = ArrayCarregaSituacao[14];
		int iSemNor = ArrayCarregaSituacao[15];
		
		int iSitFal = ArrayCarregaSituacao[16];
		int iSitAtr = ArrayCarregaSituacao[17];
		int iSitSaA = ArrayCarregaSituacao[18];
		int iFalPar = ArrayCarregaSituacao[19];
		
		int iNaoRef = ArrayCarregaSituacao[20];
		int iExtExc = ArrayCarregaSituacao[21];
		int iM6hInt = ArrayCarregaSituacao[22];
		int iTraFol = ArrayCarregaSituacao[23];
		int iTraFer = ArrayCarregaSituacao[24];
		int iTraAfa = ArrayCarregaSituacao[25];
		int iLimCom = ArrayCarregaSituacao[26];
		
		int iCurApr = ArrayCarregaSituacao[27];
		int iExtApr = ArrayCarregaSituacao[28];
		int iAusApr = ArrayCarregaSituacao[29];
		int iExt1236 = ArrayCarregaSituacao[30];
		int iDSR1236 = ArrayCarregaSituacao[31];
		int iAus1236 = ArrayCarregaSituacao[32];
		
		int iSitTraCmp = ArrayCarregaSituacao[33];
		int iNorAntCmp = ArrayCarregaSituacao[34];
		int iSepAntCmp = ArrayCarregaSituacao[35];
		int iNorFolCmp = ArrayCarregaSituacao[36];
		int iSepFolCmp = ArrayCarregaSituacao[37];
		int iNorComCmp = ArrayCarregaSituacao[38];
		int iSepComCmp = ArrayCarregaSituacao[39];
		int iNorDesCmp = ArrayCarregaSituacao[40];
		int iNorFerCmp = ArrayCarregaSituacao[41];
		
		int iExt10hCmp = ArrayCarregaSituacao[42]; 
		int iExtAciCmp = ArrayCarregaSituacao[43]; 
		int iExt10hComCmp = ArrayCarregaSituacao[44];
		int iSemNorCmp = ArrayCarregaSituacao[45];
		int iExt1236Cmp = ArrayCarregaSituacao[46];
		int iDSR1236Cmp = ArrayCarregaSituacao[47];
		
		//Menor Aprendiz
		if (iTipCon == 6) {
			MenorAprendiz(iCodEsc, iSitFal, iSitAtr, iCurApr, iExtApr, iAusApr, iNorAnt, iCodHor);
		}
		
		if(iQtdMar > 0){
			//Marcações Ímpares
			if(iSitNid > 0)
				MarcacoesInvalidas(iSitNid, iQtdMar);
		
			//Falta Parcial
			if(iFalPar > 0)
				FaltaParcial(iSitFal, iFalPar, iFatRed);
			
			//Tolerância de Horários
			if(iCodHor < 9996)
				ToleranciasDiaria (iToEHor, iToFHor, iSitTra, iSitTraCmp, iCodHor);
		
			//Não Cumprimento de Refeição Integral
			if((iCodHor < 9996) && (iNaoRef > 0) && (CxApuracao.getHorSit(iSitNid) == 0) && (iClaEsc != 3))
				NaoCumprimentoRefeicao (iSitFal, iNaoRef, iFatRed);
			
			//+ 6H Trabalhadas sem Intervalo
			if(iM6hInt > 0)
				Mais6HorasTrabSeguida (iM6hInt, iNumEmp, iTipCol, iNumCad, iQtdMar);
		
			//Extras Excedidas Diárias
			if((iExtExc > 0) && (iCodHor < 9996))
				ExtraExcedenteDiaria (iCodHor, iExtExc, iNorAnt, iNorAntCmp, iSepAnt, iSepAntCmp, iFatRed);
		
			//Gerar Horas Extras - Acima da 10 hora trabalhada
			if(iCodHor < 9996)
				ExtraAcima10Horas (iFatRed, iNorAnt, iNorAntCmp, iExt10h, iExt10hCmp, iSep10h, iExtAci, iExtAciCmp);
			
			//Gerar Horas Extras - Acima da 10 hora trabalhada em dias Compensados e Folgas
			if(((iCodHor == 9998) || (iCodHor == 9996)) && (iExt10hCom > 0)){
				ExtraAcima10HorasFolgas (iCodHor, iFatRed, iNorCom, iNorComCmp, iSepCom, iSepComCmp, iNorFol, iNorFolCmp, iSepFol, iSepFolCmp, iExt10hCom, iExt10hComCmp, iSep10h);
			}
			
			//Alterar Situação de Hora Extra para Escalas 12x36
			if((iClaEsc == 1) || (iClaEsc == 2) || (iClaEsc == 3))
				ExtrasAusencias12x36(iSitFal, iSitAtr, iExt1236, iExt1236Cmp, iDSR1236, iDSR1236Cmp, iAus1236, iNorAnt, iNorAntCmp, iNorDes, iNorDesCmp, iNorFer, iNorFerCmp, iCodHor);

			
			//Trabalho na Folga
			if((iTraFol > 0) && ((iCodHor == 9996) || (iCodHor == 9998) || (iCodHor == 9999))){
				TrabalhoFolga (iTraFol, iCodHor, iNorFol, iNorFolCmp, iSepFol, iSepFolCmp, iNorCom, iSepCom, iSepComCmp, iNorComCmp, iNorDes, iNorDesCmp);
			}
		
			//Trabalho no Feriado
			if((iTraFer > 0) && (iCodHor == 9997)){
				TrabalhoFeriado(iTraFer, iCodHor, iNorFer, iNorFerCmp);
			}
		
			//Trabalho no Afastamento
			if(iTraAfa > 0)
				TrabalhoAfastamento (iTraAfa, iCodHor, iSitTra, iSitTraCmp, iNorAnt, iNorAntCmp, iNorFol, iNorFolCmp, iNorCom, iNorComCmp, iNorDes, iNorDesCmp, iNorFer, iNorFerCmp);
			
			//Saldo Semanal de Banco de Horas
			if(iSemNor > 0)
				BancoHorasSemanal(iNumEmp, iTipCol, iNumCad, iQtdMar, iSitTra, iSitTraCmp, iNorAnt, iNorAntCmp, iSepAnt, iSepAntCmp, iSemNor, iSemNorCmp);
			
			//Décimo Quarto dia de Plantão
			if(iClaEsc == 1) {
				DecimoQuartoDiaTrabalhado12x36(iNumEmp, iTipCol, iNumCad, iNorCom, iNorComCmp, iSepCom, iSepComCmp, iLimCom, iSitTra, iSitTraCmp, iNorAnt, iNorAntCmp, iSepAnt, iSepAntCmp, iSitAtr);
			}
			
		}
		
		//Feriado Compensado
		//if(iNumCad != 20004273)
		FeriadoCompensado3 (iCodEsc, iNumEmp, iTipCol, iNumCad, iNorFer, iSitAtr, iSitSaA);

		}
	
public int[] BuscaCamposTolDiara (int CodHor) {
	int ToEHor = 0 ;
	int ToFHor = 0 ;
	int TipHor = 0 ;
		
	IEntitySession entitySession = EntitySessionProvider.getSession();
	MappedParamProvider Param = new MappedParamProvider();
		
		
	Param.setParam("CodHor",CodHor);
	ICursor<IR004HOR> cIR004HOR = entitySession.newCursor(IR004HOR.class);
	cIR004HOR.addFilter("CodHor = :CodHor ", Param);
	cIR004HOR.open();
	try {
		if (cIR004HOR.first()) {
			IR004HOR R004HOR = cIR004HOR.read();
				
			if (R004HOR.isToFHorNull() ==true){
			   ToFHor = 0 ;
			} else 
				{
			    ToFHor = R004HOR.getToFHor();
				}
			    
			if (R004HOR.isToEHorNull() == true) {
			    ToEHor = 0 ;
			} else 
				{
			    ToEHor = R004HOR.getToEHor();
			    }
			    
			if (R004HOR.isTipHorNull()== true) {
			    TipHor = 0 ;
			} else  
				{
			    	TipHor = R004HOR.getTipHor() ;
			    }
			    
		}
	} 
		finally {
			cIR004HOR.close(); 	
		}
		 
	int[] retorno = new int[] {ToEHor,ToFHor};
	return retorno ;
} 
	
public int RetornaSituacaoComplementar(int CodSit){
	IEntitySession entitySession = EntitySessionProvider.getSession();
	
	int CodSitCmp = 0;
	
	MappedParamProvider Param = new MappedParamProvider();
	Param.setParam("CodSit",CodSit);
	ICursor<IR010SIT> cIR010SIT = entitySession.newCursor(IR010SIT.class);
	cIR010SIT.addFilter("CodSit = :CodSit ", Param);
	cIR010SIT.open();
	try {
		if (cIR010SIT.first()) {
			IR010SIT	 R010SIT = cIR010SIT.read();
					
			if (R010SIT.isSitCmpNull() ==true) {
				CodSitCmp =  0;
			}
			else {
				CodSitCmp = R010SIT.getSitCmp();
				}
		}
				
	}
		finally {
			cIR010SIT.close();
		}
	
	return CodSitCmp;
}

public int[] CarregaSituacaoes(){
	  	
	int SitTra = 0; //Situação Trabalho
	int AdiNot = 0; //Adicional Noturno;
	int SitNid = 0; //Marcaçoes Inválidas;
	int NorAnt = 0; //Situação Horas Extras Dias Normais
	int SepAnt = 0; //Situação Horas Extras Dias Normais - Separação
	int NorFol = 0; //Extra na Folga
	int SepFol = 0; //Separação na Folga
	int NorCom = 0; //Extra no Compensado
	int SepCom = 0; //Separação no Compensado
	int NorDes = 0; //Extra DSR
	int NorFer = 0; //Extra Feriado
	int SitFal = 0; //Falta
	int SitAtr = 0; //Atraso
	int SitSaA = 0; //Saída Antecipada
	
	int LimCom = 0; //Qtd Horas Separação Compensado
	
	int FalPar = 0; //Falta Parcial
	int NaoRef = 0; //Não Cumprimento Integral Refeição
	int ExtExc = 0; //Extra Excedente
	int M6hInt = 0; //+ 6h Trabalhadas sem Intervalo
	
	int Ext10h = 0; //Extra Acima 10h
	int Sep10h = 0; //Separação Extra Acima 10° hora
	int ExtAci = 0; //Extras Após Separação 10° hora 
	int Ext10hCom = 0; //Extras Acima 10h em Compensados
	
	int SemNor = 0; //Situação de Extra Após Limite Semanal BH - Dias Normais
	
	int TraFer = 0; //Trabalho no Feriado 
	int TraFol = 0; //Trabalho na Folga
	int TraAfa = 0; //Trabalho no Afastamento
	
	int Ext1236 = 0; //Situação de Extras 12x36 - Dias Normais
	int DSR1236 = 0; //Situação de Extras 12x36 - DSR/Feriado
	int Aus1236 = 0; //Situação de Ausências 12x36

	int SitTraCmp = 0; //Situação Trabalho Noturno
	int NorAntCmp = 0; //Situação Horas Extras Dias Normais Noturno
	int SepAntCmp = 0; //Situação Horas Extras Dias Normais - Separação Noturno	
	int NorFolCmp = 0; //Situação Horas Extras Folga Noturno
	int SepFolCmp = 0; //Situação Horas Extras Separação na Folga Noturno
	int NorComCmp = 0; //Situação Horas Extras Compensado Noturno
	int SepComCmp = 0; //Situação Horas Extras Separação no Compensado
	int NorDesCmp = 0; //Situação Horas Extras DSR Noturno
	int NorFerCmp = 0; //Situação Horas Extras Feriado Noturno
	int SemNorCmp = 0; //Situação de Extra Após Limite Semanal BH - Dias Normais (Noturno)
	int Ext1236Cmp = 0;
	int DSR1236Cmp = 0;
	
	int Ext10hCmp = 0;
	int ExtAciCmp = 0;
	int Ext10hComCmp = 0; 
	
	int CurApr = 0; //Situação Curso Aprendiz
	int ExtApr = 0; //Situação Hora Extra Aprendiz
	int AusApr = 0; //Situação Ausência Aprendiz
	
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	
	LocalDate dDatPro = CxApuracao.getData();
	int CodDSi = CxApuracao.getDefinicaoSituacoes().getCodigo();
			
	IEntitySession entitySession = EntitySessionProvider.getSession();
	MappedParamProvider Param = new MappedParamProvider();
	
	Param.setParam("CodDSi",CodDSi);
	Param.setParam("DatAlt",dDatPro);
	ICursor<IR060DSICustom> cIR060DSI = entitySession.newCursor(IR060DSICustom.class);
	cIR060DSI.addFilter("CodDSi = :CodDSi",Param);
	cIR060DSI.addFilter("DatAlt <= :DatAlt",Param);
	
	String[] campos = new String[1];
	campos[0] = "DatAlt";
	OrderDirection[] ordem = new OrderDirection[1];
	ordem[0] = OrderDirection.DESC;
	cIR060DSI.setOrder(campos, ordem);
	
	cIR060DSI.open();
	try {
		if(cIR060DSI.first()) {
			IR060DSICustom R060DSI = cIR060DSI.read();
						
			// Situação de Trabalho
			if (R060DSI.isSitTraNull() == true) {
				SitTra = 0 ;
			} else {
				SitTra = R060DSI.getSitTra() ;
			}
			
			// Adicional Noturno
			if (R060DSI.isSitAdiNull() == true) {
				AdiNot = 0 ;
			} else {
				AdiNot = R060DSI.getSitAdi() ;
			}
			
			//Marcações Inválidas
			if (R060DSI.isSitNIdNull() == true) {
				SitNid = 0 ;	
			} else {
				SitNid = R060DSI.getSitNId() ;	
			}
			
		    //Situação Extra Dias Normais
			if (R060DSI.isNorAntNull() == true) {
				NorAnt = 0 ;	
			} else {
				NorAnt = R060DSI.getNorAnt() ;	
			}
			
		    //Situação Extra Dias Normais - Separação
			if (R060DSI.isSepAntNull() == true) {
				SepAnt = 0 ;	
			} else {
				SepAnt = R060DSI.getSepAnt() ;	
			}			

			//Situação Extra Folga
			if (R060DSI.isNorFolNull() == true) {
				NorFol = 0 ;	
			} else {
				NorFol = R060DSI.getNorFol();	
			}
			
			//Situação Separação na Folga
			if (R060DSI.isSepFlgNull() == true) {
				SepFol = 0 ;	
			} else {
				SepFol = R060DSI.getSepFlg();	
			}

			//Situação Extra Compensado
			if (R060DSI.isNorComNull() == true) {
				NorCom = 0 ;	
			} else {
				NorCom = R060DSI.getNorCom();	
			}
			
			//Situação Separação no Compensado
			if (R060DSI.isSepComNull() == true) {
				SepCom = 0 ;	
			} else {
				SepCom = R060DSI.getSepCom();	
			}
			
			//Situação Extra Compensado
			if (R060DSI.isNorComNull() == true) {
				NorCom = 0 ;	
			} else {
				NorCom = R060DSI.getNorCom();	
			}
			
			//Limite de Horas de Separação no Compensado
			if (R060DSI.isLimComNull() == true) {
				LimCom = 0 ;	
			} else {
				LimCom = R060DSI.getLimCom();	
			}
		
			//Situação Extra Feriados
			if (R060DSI.isNorFerNull() == true) {
				NorFer = 0 ;	
			} else {
				NorFer = R060DSI.getNorFer();	
			}
			
			//Situação Extra Descanso
			if (R060DSI.isNorDesNull() == true) {
				NorDes = 0 ;	
			} else {
				NorDes = R060DSI.getNorDes();	
			}
			
			// Falta
			if (R060DSI.isSitFalNull() == true) {
				SitFal = 0 ;
			} else {
				SitFal = R060DSI.getSitFal() ;
			}
			
			//Situação de Atraso
			if (R060DSI.isSitAtrNull() == true) {
				SitAtr = 0 ;
			} else {
				SitAtr = R060DSI.getSitAtr() ;
			}
			
			//Situação de Saída Antecipada
			if (R060DSI.isSitSaANull() == true) {
				SitSaA = 0 ;
			} else {
				SitSaA = R060DSI.getSitSaA() ;
			}
			
			
			//Situação Falta Parcial
			if (R060DSI.isUSU_FalParNull() == true) {
				FalPar = 0 ;	
			} else {
				FalPar = R060DSI.getUSU_FalPar() ;	
			}
			
			//Não Cumprimento de Refeição
			if (R060DSI.isUSU_NaoRefNull() == true) {
				NaoRef = 0 ;	
			} else {
				NaoRef = R060DSI.getUSU_NaoRef() ;	
			}
			
			//Extras Excedidas Diária
			if (R060DSI.isUSU_ExtExcNull() == true) {
				ExtExc = 0 ;	
			} else {
				ExtExc = R060DSI.getUSU_ExtExc() ;	
			}
			
			//+ 6h Trabalhadas Seguidas
			if (R060DSI.isUSU_M6hIntNull() == true) {
				M6hInt = 0 ;	
			} else {
				M6hInt = R060DSI.getUSU_M6hInt() ;	
			}
			
			//Trabalho na Folga
			if (R060DSI.isUSU_TraFolNull() == true) {
				TraFol = 0 ;	
			} else {
				TraFol = R060DSI.getUSU_TraFol() ;	
			}
			
			//Trabalho no Feriado
			if (R060DSI.isUSU_TraFerNull() == true) {
				TraFer = 0 ;	
			} else {
				TraFer = R060DSI.getUSU_TraFer() ;	
			}
			
			//Trabalho no Afastamento
			if (R060DSI.isUSU_TraAfaNull() == true) {
				TraAfa = 0 ;	
			} else {
				TraAfa = R060DSI.getUSU_TraAfa() ;	
			}
			
			//Extras até 10° hora
			if (R060DSI.isUSU_Ext10hNull() == true) {
				Ext10h = 0 ;	
			} else {
				Ext10h = R060DSI.getUSU_Ext10h() ;	
			}
			
			//Separação extra depois 10° hora
			if (R060DSI.isUSU_Sep10hNull() == true) {
				Sep10h = 0 ;	
			} else {
				Sep10h = R060DSI.getUSU_Sep10h() ;	
			}
			
			//Extra depois 10° hora
			if (R060DSI.isUSU_ExtAciNull() == true) {
				ExtAci = 0 ;	
			} else {
				ExtAci = R060DSI.getUSU_ExtAci() ;	
			}
			
			//Extra Acima 10° - Dias Compensados
			if (R060DSI.isUSU_Ext10hCmpNull() == true) {
				Ext10hCom = 0 ;	
			} else {
				Ext10hCom = R060DSI.getUSU_Ext10hCmp() ;	
			}
			
			//Extra Semanal - Dias Normais
			if (R060DSI.isUSU_SemNorNull() == true) {
				SemNor = 0 ;	
			} else {
				SemNor = R060DSI.getUSU_SemNor() ;	
			}
			
			//Aprendiz - Curso
			if (R060DSI.isUSU_CurAprNull() == true) {
				CurApr = 0 ;	
			} else {
				CurApr = R060DSI.getUSU_CurApr() ;	
			}
			
			//Aprendiz - Extra
			if (R060DSI.isUSU_ExtAprNull() == true) {
				ExtApr = 0 ;	
			} else {
				ExtApr = R060DSI.getUSU_ExtApr() ;	
			}
			
			//Aprendiz - Ausência
			if (R060DSI.isUSU_AusAprNull() == true) {
				AusApr = 0 ;	
			} else {
				AusApr = R060DSI.getUSU_AusApr() ;	
			}
			
			//Aprendiz - Ausência
			if (R060DSI.isUSU_AusAprNull() == true) {
				AusApr = 0 ;	
			} else {
				AusApr = R060DSI.getUSU_AusApr() ;	
			}
			
			//12x36 - Extras Dias Normais
			if (R060DSI.isUSU_Ext1236Null() == true) {
				Ext1236 = 0 ;	
			} else {
				Ext1236 = R060DSI.getUSU_Ext1236() ;	
			}
			
			//12x36 - Extras DSR
			if (R060DSI.isUSU_DSR1236Null() == true) {
				DSR1236 = 0 ;	
			} else {
				DSR1236 = R060DSI.getUSU_DSR1236() ;	
			}
			
			//12x36 - Ausencia
			if (R060DSI.isUSU_Aus1236Null() == true) {
				Aus1236 = 0 ;	
			} else {
				Aus1236 = R060DSI.getUSU_Aus1236() ;	
			}
			
			
			
		}
	}
		finally {
			cIR060DSI.close();
		}
			
	//SITUAÇÕES COMPLMENTARES
			
	SitTraCmp = RetornaSituacaoComplementar(SitTra);		
	NorAntCmp = RetornaSituacaoComplementar(NorAnt);
	SepAntCmp = RetornaSituacaoComplementar(SepAnt);
	NorFolCmp = RetornaSituacaoComplementar(NorFol);
	SepFolCmp = RetornaSituacaoComplementar(SepFol);
	NorComCmp = RetornaSituacaoComplementar(NorCom);
	SepComCmp = RetornaSituacaoComplementar(SepCom);
    NorFerCmp = RetornaSituacaoComplementar(NorFer);
    NorDesCmp = RetornaSituacaoComplementar(NorDes);
    Ext10hCmp = RetornaSituacaoComplementar(Ext10h);
    ExtAciCmp = RetornaSituacaoComplementar(ExtAci);
	Ext10hComCmp = RetornaSituacaoComplementar(Ext10hCom);
	SemNorCmp = RetornaSituacaoComplementar(SemNor);
	Ext1236Cmp = RetornaSituacaoComplementar(Ext1236);
	DSR1236Cmp = RetornaSituacaoComplementar(DSR1236);
		
	int[] retorno = new int[] {SitTra, AdiNot, SitNid, NorAnt, SepAnt, NorFol, SepFol, NorCom, SepCom, NorDes, NorFer, Ext10h, Sep10h, ExtAci, Ext10hCom, SemNor,
			                   SitFal, SitAtr, SitSaA, FalPar, NaoRef, ExtExc, M6hInt, TraFol, TraFer, TraAfa, LimCom, CurApr, ExtApr, AusApr, Ext1236, DSR1236, Aus1236,
			                   SitTraCmp, NorAntCmp, SepAntCmp, NorFolCmp, SepFolCmp, NorComCmp, SepComCmp, NorDesCmp, NorFerCmp, Ext10hCmp, ExtAciCmp, Ext10hComCmp, SemNorCmp, Ext1236Cmp, DSR1236Cmp};
			return retorno;
			
		}

public void MarcacoesInvalidas (int SitNid, int QtdMar) {
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	
	CxApuracao.zeraHorasSituacao(SitNid);
	
	if (QtdMar%2 != 0) {
		CxApuracao.setHorSit(SitNid,1); 	
	}
	
}

public void ToleranciasDiaria (int ToEHor, int ToFHor, int SitTra, int SitTraCmp, int CodHor) {
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	
	IEntitySession entitySession = EntitySessionProvider.getSession();
	MappedParamProvider Param = new MappedParamProvider();
	
	List<MarcacaoRegra> marcacoesRealizadas =  CxApuracao.getMarcacoesRealizadas(true);
	
	int TotFal = 0 ;
	int TotExt = 0 ;

	TotFal = CxApuracao.getTotalSituacoes(TOTALIZADOR_HORAFALTA, CxApuracao.getData());
	TotExt = CxApuracao.getTotalSituacoes(TOTALIZADOR_HORAEXTRA, CxApuracao.getData());
	
	//Verificar e Zerar extras menores que a tolerância
	if ((TotExt <= ToEHor ) && (TotExt > 0)) {
		Param.setParam("CodTot",TOTALIZADOR_HORAEXTRA);
		ICursor<IR010TOB> cIR010TOB = entitySession.newCursor(IR010TOB.class);
		cIR010TOB.addFilter("CodTot = :CodTot ", Param);
		cIR010TOB.open();
		try {
			while (cIR010TOB.next()) {
				IR010TOB Totalizador = cIR010TOB.read();
				int  ncodSit = Totalizador.getCodSit();
				CxApuracao.zeraHorasSituacao(ncodSit);
			}
		}
		finally {
			cIR010TOB.close();
		} 		
	}
		
	if ((TotFal <= ToFHor) &&  (TotFal != 0)) {
		Param.setParam("CodTot",TOTALIZADOR_HORAFALTA);
		ICursor<IR010TOB> cIR010TOB = entitySession.newCursor(IR010TOB.class);
		cIR010TOB.addFilter("CodTot = :CodTot ", Param);
		cIR010TOB.open();
		try {
			while (cIR010TOB.next()) {
				IR010TOB Totalizador = cIR010TOB.read();
				int  nCodSit = Totalizador.getCodSit();
					 
				if ((CxApuracao.getHorSit(SitTra) > 0) && (CxApuracao.getHorSit(nCodSit) > 0)) {
					CxApuracao.setHorSit(SitTra,  CxApuracao.getHorSit(SitTra,nCodSit));	 
				}
				else {
					if ((CxApuracao.getHorSit(SitTraCmp) > 0) && (CxApuracao.getHorSit(nCodSit) > 0)) {
						CxApuracao.setHorSit(SitTraCmp, CxApuracao.getHorSit(051,nCodSit));
						CxApuracao.zeraHorasSituacao(nCodSit);	  
					}
						 
				}
					 
			}
		}
		finally {
			cIR010TOB.close();
			} 
	}		
}

public void FaltaParcial (int SitFal, int FalPar, int FatRed){
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	if((CxApuracao.getHorSit(SitFal) > 0 ) && (CxApuracao.getHorSit(SitFal) != FatRed) && (FalPar != 0)){
		
		CxApuracao.setHorSit(FalPar, CxApuracao.getHorSit(FalPar,SitFal));
		CxApuracao.setHorSit(SitFal, 0);	
	}
}

public void ExtraExcedenteDiaria (int CodHor, int LimExt, int NorAnt, int NorAntCmp, int SepAnt, int SepAntCmp, int FatRed){
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	
	int iTotExt = CxApuracao.getTotalSituacoes(TOTALIZADOR_HORAEXTRA, CxApuracao.getData());
	
	int iLimTra = 600; //Limite máximo de horas possível trabalhadas no dia
	int iLimHE = 0;    //Limte máximo de Horas extras
	
	if(iLimTra >= FatRed){
		iLimHE = iLimTra - FatRed;
	}else
	    iLimHE = 0;
	    
	if(iLimHE > 120){ //Não pode ser maior que 2 horas diárias@
		iLimHE = 120;
	}
	CxApuracao.zeraHorasSituacao(LimExt);   
	 
	 
	if(iTotExt > iLimHE){
		CxApuracao.setHorSit(LimExt, iTotExt - iLimHE);
	}
	
	
}

public void Mais6HorasTrabSeguida (int M6hInt, int NumEmp, int TipCol, int NumCad, int QtdMar){
	  ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	  
	  if((M6hInt > 0) && (QtdMar > 0)){
		  int x = 0;
		  int y = 0;
		  LocalDate dDatMar1 = CxApuracao.getData();
		  LocalDate dDatMar2 = CxApuracao.getData();
		  int HorMar1 = 0;
		  int HorMar2 = 0;
		  int iTotTra = 0;
	  
		  //Verifica as horas de trabalho realizados
		  x = 0;
		  y = 1;
		  int QtdMin = 0;
		  while(y < QtdMar){
			  dDatMar1 = CxApuracao.getMarcacoesRealizadas(true).get(x).getData().toLocalDate();
			  dDatMar2 = CxApuracao.getMarcacoesRealizadas(true).get(y).getData().toLocalDate();
			  HorMar1 = CxApuracao.getMarcacoesRealizadas(true).get(x).getHora();
			  HorMar2 = CxApuracao.getMarcacoesRealizadas(true).get(y).getHora();
			  
			  //Diferença entre as duas datas e horas (convertidas em minutos)
			  QtdMin = CalculaQtdMinutos (dDatMar1, HorMar1, dDatMar2, HorMar2);
		 	 
			  if(QtdMin > 360){ //Realizou Trabalho maior que 6 horas seguidas
				  CxApuracao.setHorSit(M6hInt, QtdMin - 360);
			  }
			  
			  x = x + 2;
			  y = y + 2;
		  }
	  }
}

public void NaoCumprimentoRefeicao (int SitFal, int NaoRef, int FatRed){
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	TipoIntervalo intervalo = null;
	
	List<Compensacao> compensacoes = CxApuracao.getCompensacoes(CxApuracao.getData());
	List <HistoricoAfastamento> listaAfastamentos = CxApuracao.getHistoricosAfastamento();
	
	int iRefPre = CxApuracao.getMinutosRefeicaoPrevisto();
	ISeparacaoHoras separacaoHoras = CxApuracao.getHorasSeparadas(intervalo.REFEICAO);
	int iRefRea = separacaoHoras.getHorasDiurnas() + separacaoHoras.getHorasNoturnas();
	
	if((iRefPre > 0) && (iRefRea == 0)){
		if((compensacoes.size() == 0) && (listaAfastamentos.size() == 0) && (CxApuracao.getHorSit(SitFal) != FatRed)){
	       CxApuracao.setHorSit(NaoRef, iRefPre);   
	    }else
	        CxApuracao.setHorSit(NaoRef, 0);
	  }
}

public void TrabalhoFolga (int TraFol, int CodHor, int NorFol, int NorFolCmp, int SepFol, int SepFolCmp, int NorCom, int SepCom, int SepComCmp, int NorComCmp, int NorDes, int NorDesCmp){
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();

	if(CodHor == 9996){
		CxApuracao.setHorSit(TraFol, CxApuracao.getHorSit(NorFol) + CxApuracao.getHorSit(NorFolCmp));
		if(SepFol > 0)
			CxApuracao.setHorSit(TraFol, CxApuracao.getHorSit(TraFol) + CxApuracao.getHorSit(SepFol) + CxApuracao.getHorSit(SepFolCmp));
	}else
		if(CodHor == 9998){
			CxApuracao.setHorSit(TraFol, CxApuracao.getHorSit(NorCom) + CxApuracao.getHorSit(NorComCmp));
			if(SepCom > 0)
				CxApuracao.setHorSit(TraFol, CxApuracao.getHorSit(TraFol) + CxApuracao.getHorSit(SepCom) + CxApuracao.getHorSit(SepComCmp));
				
	}else
		if(CodHor == 9999){
			CxApuracao.setHorSit(TraFol, CxApuracao.getHorSit(NorDes) + CxApuracao.getHorSit(NorDesCmp));
		}
	
}

public void TrabalhoFeriado (int TraFol, int CodHor, int NorFer, int NorFerCmp){
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
		
	CxApuracao.setHorSit(TraFol, CxApuracao.getHorSit(NorFer) + CxApuracao.getHorSit(NorFerCmp));	
}

public void TrabalhoAfastamento (int TraAfa, int CodHor, int SitTra, int SitTraCmp, int NorAnt, int NorAntCmp, int NorFol,
		 int NorFolCmp, int NorCom, int NorComCmp, int NorDes, int NorDesCmp, int NorFer, int NorFerCmp){

	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();

	int iQtdMar = CxApuracao.getMarcacoesRealizadas(true).size();
	int iCodAfs = 0;
	int iTipSit = 0;
	List <HistoricoAfastamento> listaAfastamentos = CxApuracao.getHistoricosAfastamento();

	if((iQtdMar > 0) && (listaAfastamentos.size() > 0)){

		for (HistoricoAfastamento HisAfas :listaAfastamentos) {
			iCodAfs = HisAfas.getSitAfa();

			IEntitySession entitySession = EntitySessionProvider.getSession();
			MappedParamProvider Param1 = new MappedParamProvider();
			Param1.setParam("CodAfs",iCodAfs);
			ICursor<IR010SIT> cIR010SIT1 = entitySession.newCursor(IR010SIT.class);
			cIR010SIT1.addFilter("CodSit = :CodAfs ", Param1);
			cIR010SIT1.open();
			try {
				if (cIR010SIT1.first()){
					IR010SIT	 R010SIT = cIR010SIT1.read();

					if (R010SIT.isTipSitNull() ==true){
						iTipSit =  0;
					}
					else 
					{
						iTipSit = R010SIT.getTipSit();	
					}
				}

			}
			finally{
				cIR010SIT1.close();
			}

			if((iTipSit == 2) || (iTipSit == 3)  || (iTipSit == 4)  || (iTipSit == 5)  || (iTipSit == 6)  || (iTipSit == 8) || 
					(iTipSit == 9) || (iTipSit == 10) || (iTipSit == 11) || (iTipSit == 12) || (iTipSit == 19) || (iTipSit == 22)){ 

				if(CodHor < 9996){
					CxApuracao.setHorSit(TraAfa, CxApuracao.getHorSit(SitTra) + 
							CxApuracao.getHorSit(SitTraCmp) + 
							CxApuracao.getHorSit(NorAnt) + 
							CxApuracao.getHorSit(NorAntCmp));
				}else
					if(CodHor == 9996){
						CxApuracao.setHorSit(TraAfa, CxApuracao.getHorSit(NorFol) + CxApuracao.getHorSit(NorFolCmp));
					}else
						if(CodHor == 9997){
							CxApuracao.setHorSit(TraAfa, CxApuracao.getHorSit(SitTra) + 
									CxApuracao.getHorSit(SitTraCmp) + 
									CxApuracao.getHorSit(NorAnt) + 
									CxApuracao.getHorSit(NorAntCmp) +
									CxApuracao.getHorSit(NorFerCmp) +
									CxApuracao.getHorSit(NorFerCmp));	
						}else
							if(CodHor == 9998){
								CxApuracao.setHorSit(TraAfa, CxApuracao.getHorSit(NorCom) + CxApuracao.getHorSit(NorComCmp));	
							}else
								if(CodHor == 9999){
									CxApuracao.setHorSit(TraAfa, CxApuracao.getHorSit(NorDes) + CxApuracao.getHorSit(NorDesCmp));	
								}
			}
		}
	}
}

public void ExtraAcima10Horas (int FatRed, int NorAnt, int NorAntCmp, int Ext10h, int Ext10hCmp, int Sep10h, int ExtAci, int ExtAciCmp){
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	
	int TotExt = CxApuracao.getTotalSituacoes(TOTALIZADOR_HORAEXTRA, CxApuracao.getData());
	
	int LimTra = 600; //Limite máximo de horas possível trabalhadas no dia
	int LimHE = 0;    //Limte máximo de Horas extras
	
	if(LimTra >= FatRed){
		LimHE = LimTra - FatRed;
	}else
	    LimHE = 0;
	
	if(LimHE > 120){ //Não pode ser maior que 2 horas diárias@
		LimHE = 120;
	}
	
	if((TotExt > LimHE) && (Ext10h > 0) && (Ext10hCmp > 0)){
		int HorExc = TotExt - LimHE;
		
		//Substitui situação noturna
		if((CxApuracao.getHorSit(NorAntCmp) >= HorExc) && (HorExc > 0)){
			CxApuracao.setHorSit(Ext10hCmp, HorExc);
			CxApuracao.setHorSit(NorAntCmp, CxApuracao.getHorSit(NorAntCmp) - HorExc);
			HorExc = 0;
		}else
			if((CxApuracao.getHorSit(NorAntCmp) < HorExc) && (CxApuracao.getHorSit(NorAntCmp) > 0) && (HorExc > 0)){
				CxApuracao.setHorSit(Ext10hCmp, CxApuracao.getHorSit(NorAntCmp));
				HorExc = HorExc - CxApuracao.getHorSit(NorAntCmp);
				CxApuracao.zeraHorasSituacao(NorAntCmp);
			}
		
		//Substitui situação Diurna
		if((CxApuracao.getHorSit(NorAnt) >= HorExc) && (HorExc > 0)){
			CxApuracao.setHorSit(Ext10h, HorExc);
			CxApuracao.setHorSit(NorAnt, CxApuracao.getHorSit(NorAnt) - HorExc);
			HorExc = 0;
		}else
			if((CxApuracao.getHorSit(NorAnt) < HorExc) && (CxApuracao.getHorSit(NorAnt) > 0) && (HorExc > 0)){
				CxApuracao.setHorSit(Ext10h, CxApuracao.getHorSit(NorAnt));
				HorExc = HorExc - CxApuracao.getHorSit(NorAnt);
				CxApuracao.zeraHorasSituacao(NorAnt);
			}
		
		
		
		
		//Após as 10 horas dia, se tem mais uma quebra
		if((Sep10h > 0) && (ExtAci > 0) && (ExtAciCmp > 0) && (TotExt > LimHE + Sep10h)){
			
			HorExc = TotExt - ((LimHE + Sep10h));
			
			//Substitui situação noturna
			if((CxApuracao.getHorSit(Ext10hCmp) >= HorExc) && (HorExc > 0)){
				CxApuracao.setHorSit(ExtAciCmp, HorExc);
				CxApuracao.setHorSit(Ext10hCmp, CxApuracao.getHorSit(Ext10hCmp) - HorExc);
				HorExc = 0;
			}else
				if((CxApuracao.getHorSit(Ext10hCmp) < HorExc) && (CxApuracao.getHorSit(Ext10hCmp) > 0) && (HorExc > 0)){
					CxApuracao.setHorSit(ExtAciCmp, CxApuracao.getHorSit(Ext10hCmp));
					HorExc = HorExc - CxApuracao.getHorSit(Ext10hCmp);
					CxApuracao.zeraHorasSituacao(Ext10hCmp);
				}
			
			//Substitui situação Diurna
			if((CxApuracao.getHorSit(Ext10h) >= HorExc) && (HorExc > 0)){
				CxApuracao.setHorSit(ExtAci, HorExc);
				CxApuracao.setHorSit(Ext10h, CxApuracao.getHorSit(Ext10h) - HorExc);
				HorExc = 0;
			}else
				if((CxApuracao.getHorSit(Ext10h) < HorExc) && (CxApuracao.getHorSit(Ext10h) > 0) && (HorExc > 0)){
					CxApuracao.setHorSit(ExtAci, CxApuracao.getHorSit(Ext10h));
					HorExc = HorExc - CxApuracao.getHorSit(Ext10h);
					CxApuracao.zeraHorasSituacao(Ext10h);
				}
			
				
		}
				
	}
		
}

public void ExtraAcima10HorasFolgas (int CodHor, int FatRed, int NorCom, int NorComCmp, int SepCom, int SepComCmp, int NorFol, int NorFolCmp, int SepFol, int SepFolCmp, int Ext10hCom, int Ext10hComCmp, int Sep10h){
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	
	int TotExt = CxApuracao.getTotalSituacoes(TOTALIZADOR_HORAEXTRA, CxApuracao.getData());
	
	int LimTra = 600; //Limite máximo de horas possível trabalhadas no dia
	int LimHE = 0;    //Limte máximo de Horas extras
	
	if(LimTra >= FatRed){
		LimHE = LimTra - FatRed;
	}else
	    LimHE = 0;
	
	if(LimHE > 120){ //Não pode ser maior que 2 horas diárias@
		LimHE = 120;
	}
	
	if((TotExt > LimHE) && (TotExt > 600) && (Ext10hCom > 0) && (Ext10hComCmp > 0)){
		int HorExc = TotExt - 600;
		
		if(CodHor == 9996) {
			if((NorFol != SepFol) && 
			   (SepFol > 0) && 
			   ((CxApuracao.getHorSit(SepFol) > 0) || (CxApuracao.getHorSit(SepFolCmp) > 0))){
			
			//Substitui situação Diurna
			if((CxApuracao.getHorSit(SepFol) >= HorExc) && (CxApuracao.getHorSit(SepFol) > 0) && (HorExc > 0)){
				CxApuracao.setHorSit(Ext10hCom, HorExc);
				CxApuracao.setHorSit(SepFol, CxApuracao.getHorSit(SepFol) - HorExc);
				HorExc = 0;
			}else
				if((CxApuracao.getHorSit(SepCom) < HorExc) && (CxApuracao.getHorSit(SepFol) > 0) && (HorExc > 0)){
					CxApuracao.setHorSit(Ext10hCom, CxApuracao.getHorSit(SepFol));
					HorExc = HorExc - CxApuracao.getHorSit(SepFol);
					CxApuracao.zeraHorasSituacao(SepFol);
				}
			
			//Substitui situação noturna
			if((CxApuracao.getHorSit(SepFolCmp) >= HorExc) && (CxApuracao.getHorSit(SepFolCmp) > 0) && (HorExc > 0)){
				CxApuracao.setHorSit(Ext10hComCmp, HorExc);
				CxApuracao.setHorSit(SepFolCmp, CxApuracao.getHorSit(SepFolCmp) - HorExc);
				HorExc = 0;
			}else
				if((CxApuracao.getHorSit(SepFolCmp) < HorExc) && (CxApuracao.getHorSit(SepFolCmp) > 0) && (HorExc > 0)){
					CxApuracao.setHorSit(Ext10hComCmp, CxApuracao.getHorSit(SepFolCmp));
					HorExc = HorExc - CxApuracao.getHorSit(SepFolCmp);
					CxApuracao.zeraHorasSituacao(SepFolCmp);
				}
			
			}else
				if((SepCom == 0) && ((CxApuracao.getHorSit(NorFol) > 0) || (CxApuracao.getHorSit(NorFolCmp) > 0))) {
				
					//Substitui situação Diurna
					if((CxApuracao.getHorSit(NorFol) >= HorExc) && (CxApuracao.getHorSit(NorFol) > 0) && (HorExc > 0)){
						CxApuracao.setHorSit(Ext10hCom, HorExc);
						CxApuracao.setHorSit(NorFol, CxApuracao.getHorSit(NorFol) - HorExc);
						HorExc = 0;
					}else
						if((CxApuracao.getHorSit(NorFol) < HorExc) && (CxApuracao.getHorSit(NorFol) > 0) && (HorExc > 0)){
							CxApuracao.setHorSit(Ext10hCom, CxApuracao.getHorSit(NorFol));
							HorExc = HorExc - CxApuracao.getHorSit(NorFol);
							CxApuracao.zeraHorasSituacao(NorFol);
						}
					
					//Substitui situação noturna
					if((CxApuracao.getHorSit(NorFolCmp) >= HorExc) && (CxApuracao.getHorSit(NorFolCmp) > 0) && (HorExc > 0)){
						CxApuracao.setHorSit(Ext10hComCmp, HorExc);
						CxApuracao.setHorSit(NorFolCmp, CxApuracao.getHorSit(NorFolCmp) - HorExc);
						HorExc = 0;
					}else
						if((CxApuracao.getHorSit(NorFolCmp) < HorExc) && (CxApuracao.getHorSit(NorFolCmp) > 0) && (HorExc > 0)){
							CxApuracao.setHorSit(Ext10hComCmp, CxApuracao.getHorSit(NorFol));
							HorExc = HorExc - CxApuracao.getHorSit(NorFolCmp);
							CxApuracao.zeraHorasSituacao(NorFolCmp);
						}	
				}
		}else
			if(CodHor == 9998) {
				if((NorCom != SepCom) && 
						   (SepCom > 0) && 
						   ((CxApuracao.getHorSit(SepCom) > 0) || (CxApuracao.getHorSit(SepComCmp) > 0))){
					
						//Substitui situação Diurna
						if((CxApuracao.getHorSit(SepCom) >= HorExc) && (CxApuracao.getHorSit(SepCom) > 0) && (HorExc > 0)){
							CxApuracao.setHorSit(Ext10hCom, HorExc);
							CxApuracao.setHorSit(SepCom, CxApuracao.getHorSit(SepCom) - HorExc);
							HorExc = 0;
						}else
							if((CxApuracao.getHorSit(SepCom) < HorExc) && (CxApuracao.getHorSit(SepCom) > 0) && (HorExc > 0)){
								CxApuracao.setHorSit(Ext10hCom, CxApuracao.getHorSit(SepCom));
								HorExc = HorExc - CxApuracao.getHorSit(SepCom);
								CxApuracao.zeraHorasSituacao(SepCom);
							}
						
						//Substitui situação noturna
						if((CxApuracao.getHorSit(SepComCmp) >= HorExc) && (CxApuracao.getHorSit(SepComCmp) > 0) && (HorExc > 0)){
							CxApuracao.setHorSit(Ext10hComCmp, HorExc);
							CxApuracao.setHorSit(SepComCmp, CxApuracao.getHorSit(SepComCmp) - HorExc);
							HorExc = 0;
						}else
							if((CxApuracao.getHorSit(SepComCmp) < HorExc) && (CxApuracao.getHorSit(SepComCmp) > 0) && (HorExc > 0)){
								CxApuracao.setHorSit(Ext10hComCmp, CxApuracao.getHorSit(SepComCmp));
								HorExc = HorExc - CxApuracao.getHorSit(SepComCmp);
								CxApuracao.zeraHorasSituacao(SepComCmp);
							}
						
						}else
							if((SepCom == 0) && ((CxApuracao.getHorSit(NorCom) > 0) || (CxApuracao.getHorSit(NorComCmp) > 0))) {
							
								//Substitui situação Diurna
								if((CxApuracao.getHorSit(NorCom) >= HorExc) && (CxApuracao.getHorSit(NorCom) > 0) && (HorExc > 0)){
									CxApuracao.setHorSit(Ext10hCom, HorExc);
									CxApuracao.setHorSit(NorCom, CxApuracao.getHorSit(NorCom) - HorExc);
									HorExc = 0;
								}else
									if((CxApuracao.getHorSit(NorCom) < HorExc) && (CxApuracao.getHorSit(NorCom) > 0) && (HorExc > 0)){
										CxApuracao.setHorSit(Ext10hCom, CxApuracao.getHorSit(NorCom));
										HorExc = HorExc - CxApuracao.getHorSit(NorCom);
										CxApuracao.zeraHorasSituacao(NorCom);
									}
								
								//Substitui situação noturna
								if((CxApuracao.getHorSit(NorComCmp) >= HorExc) && (CxApuracao.getHorSit(NorComCmp) > 0) && (HorExc > 0)){
									CxApuracao.setHorSit(Ext10hCom, HorExc);
									CxApuracao.setHorSit(NorComCmp, CxApuracao.getHorSit(NorComCmp) - HorExc);
									HorExc = 0;
								}else
									if((CxApuracao.getHorSit(NorComCmp) < HorExc) && (CxApuracao.getHorSit(NorComCmp) > 0) && (HorExc > 0)){
										CxApuracao.setHorSit(Ext10hCom, CxApuracao.getHorSit(NorComCmp));
										HorExc = HorExc - CxApuracao.getHorSit(NorComCmp);
										CxApuracao.zeraHorasSituacao(NorComCmp);
									}		
							}
					}
		
		
	
				
	}
		
}

public void FeriadoCompensado (int CodEsc, int NumEmp, int TipCol, int NumCad, int NorFer, int SitAtr, int SitSaA){
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	ContextoGeralRH CxGeral = getContainer().getContextoGeral();
	//ISeparacaoHoras separacaoHoras = CxApuracao.getHorasPrevistas(iCodHor);
	List <HistoricoAfastamento> listaAfastamentos = CxApuracao.getHistoricosAfastamento();
	int TemAfa = listaAfastamentos.size();
	int CodAfa = 0;
	if(TemAfa > 0) {
		CodAfa = listaAfastamentos.get(0).getSitAfa(); 
	}
	
	LocalDate dDatPro = CxApuracao.getData();
	LocalDate dDatSab = CxApuracao.getData();
	int HorPre = CxApuracao.getHorarioPrevisto(dDatPro);
	int CodHor = CxApuracao.getHorario().getCodigo();
	int DiaSem = dDatPro.getDayOfWeek().ordinal() + 1;
	long CarSem = CxApuracao.getEscala().getHorasSemanal();
	long HorCom = 0;
	//LocalDate dDatAdm = CxApuracao.getColaborador().getDatAdm();
	LocalDate dDatAdm = CxGeral.getColaborador(CxApuracao.getColaborador().getNumEmp(), CxApuracao.getColaborador().getTipCol(), CxApuracao.getColaborador().getNumCad()).getDatAdm();
	LocalDate dDatAux = CxApuracao.getData();
	int CmpDia = 0;
	int CarTra = 0; //Carga Horária Trabalhada
	int CarNao = 0; //Carga Horária Não Trabalhada (Abono/Afastamento)
	int CarPre = 0; //Carga Horária Prevista
	int TotPre = 0;
	int TotNao = 0;
	String aSql = "";
	String Data = "";
	
	int DiaUti = 0;
	int DiaNao = 0;
	
	int TotTra = 0;
	int TotSem = 0;
	Feriado fer = null;
	
	//Buscar a quantidade de horas a serem compensadas diariamente
	IEntitySession entitySession = EntitySessionProvider.getSession();
	MappedParamProvider Param = new MappedParamProvider();
			
	Param.setParam("CodEsc",CodEsc);
	ICursor<IR006ESC> cIR006ESC = entitySession.newCursor(IR006ESC.class);
	cIR006ESC.addFilter("CodEsc = :CodEsc ", Param);
	cIR006ESC.open();
	try {
		if (cIR006ESC.first()) {
			IR006ESC R006ESC = cIR006ESC.read();
				
			if (R006ESC.isLimBa1Null() ==true){
			   CmpDia = 0 ;
			} else 
				{
				CmpDia = R006ESC.getLimBa1();
				}    
		}
	} 
		finally {
			cIR006ESC.close(); 	
		}
	
	if(CmpDia > 0) {
	
		//Primeira Parte
		//Dia da Semana, verificar se o próximo sábado é Compesado e Feriado.
		//Se for, todas as horas ausencias serão "desconsideradas"
		
		if(DiaSem < 6) {
			dDatSab = dDatPro.plusDays(6-DiaSem);
			HorPre = CxApuracao.getHorarioPrevisto(dDatSab);
			fer = CxApuracao.getFeriado(dDatSab);
			if(fer != null) {
				if((HorPre == 9998) && (dDatSab.equals(fer.getData()))){
					//Se o sábado próximo for feriado e compensado, todas as ausências serão substituídas pela situação FeriadoCompensado
					if(CxApuracao.getHorSit(SitAtr) > 0) {
						CxApuracao.setHorSit(990, CxApuracao.getHorSit(SitAtr));
						CxApuracao.zeraHorasSituacao(SitAtr);
					}
				}
			}
		}else
			if(DiaSem == 6) {
				
				//Verificar se dia é compensado e feriado, para verificar se colaborador trabalhou a semana sem compensar ou compensando o feriado
				//Feriado fer = CxApuracao.getFeriado(dDatPro);
				HorPre = CxApuracao.getHorarioPrevisto(dDatPro);
				TotTra = 0;
				TotPre = 0;
				DiaUti = 0;
				DiaNao = 0;
				TotSem = 0;
				
				if((CodHor == 9997) && (HorPre == 9998)) {
					//Se Sábado for feriado e compensado, voltar desde o início da semana e verificar quantidade trabalhada dia a dia.
					dDatAux = dDatPro.minusDays(5);
					
					while(dDatAux.isBefore(dDatPro)) {
						if(dDatAdm.isAfter(dDatAux)) {
							DiaNao++;
						}else
						{
							
							HorPre = CxApuracao.getHorarioPrevisto(dDatAux);
							CarPre = CxApuracao.getHorasPrevistas(HorPre).getTotalHoras();
							CarNao = 0;
							
							IDBCenter database1 = DBCenter.getInstance("vetorh");
						    ISession session1 = database1.newSession();
						    aSql = "SELECT QtdHor FROM R066SIT WHERE NumEmp = ? " 
						    		+ "                          AND TipCol = ? "  
						    		+ "                          AND NumCad = ? "  
						    		+ "                          AND DatApu = ? "  
						    		+ "                          AND EXISTS"
						    		+ "                              (SELECT 1 FROM R010TOB WHERE R066SIT.CodSit = R010TOB.CodSit AND "
						    		+ "                                                           R010TOB.CodTot = 5)";
						    
						    //String Data = new java.text.SimpleDateFormat("dd/MM/yyyy").format(dDatAux.toDate());
						    DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						    Data = dDatAux.format(fmt1);
						    
						    IResultSet resultSet1 = session1.executeQuery(aSql, NumEmp, TipCol, NumCad, Data);
							try {
								if(resultSet1.next()) {
									CarNao = resultSet1.getInt(0);
									
									if(CarNao >= CarPre) {
										DiaNao++;
									}else
									{
										TotPre = TotPre + CarPre;
										TotTra = TotTra +
										DiaUti++;
									}
									
									TotNao = TotNao + CarNao;
								  }

							  }
							  finally {
								  session1.close();
							  }
							
							
							
							
							/*if(CodAfa > 999) {
								//Verificar se afastamento está na lista de afastamentos considerados
								IEntitySession entitySession2 = EntitySessionProvider.getSession();
								MappedParamProvider Param2 = new MappedParamProvider();
								Param2.setParam("CodTot",5);
								ICursor<IR010TOB> cIR010TOB = entitySession2.newCursor(IR010TOB.class);
								cIR010TOB.addFilter("CodTot = :CodTot ", Param2);
								cIR010TOB.open();
								try {
									while (cIR010TOB.next()) {
										IR010TOB Totalizador = cIR010TOB.read();
										int  CodSit = Totalizador.getCodSit();
										if(CodAfa == CodSit) {
											DiaNao++;
											CxApuracao.setHorSit(989, DiaNao);
										}
									}
								}
									finally {
										cIR010TOB.close();
									} 		
							}else*/
							if(CarNao == 0){
								//Verificar Quantidade de Horas Trabalhadas (Normais);
								//Carga Horária Prevista no Dia
								HorPre = CxApuracao.getHorarioPrevisto(dDatAux);
								CarPre = CxApuracao.getHorasPrevistas(HorPre).getTotalHoras();
								
								//Total de Horas Trabalhadas na Data
								//CarTra = CxApuracao.getTotalSituacoes(TOTALIZADOR_HORASTRAB, dDatAux);
								IDBCenter database = DBCenter.getInstance("vetorh");
							    ISession session = database.newSession();
							    aSql = "SELECT QtdHor FROM R066SIT WHERE NumEmp = ? " 
							    		+ "                          AND TipCol = ? "  
							    		+ "                          AND NumCad = ? "  
							    		+ "                          AND DatApu = ? "  
							    		+ "                          AND EXISTS"
							    		+ "                              (SELECT 1 FROM R010TOB WHERE R066SIT.CodSit = R010TOB.CodSit AND "
							    		+ "                                                              R010TOB.CodTot = 3)";
							    
							    //String Data = new java.text.SimpleDateFormat("dd/MM/yyyy").format(dDatAux.toDate());
							    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
							    Data = dDatAux.format(fmt);
							    
							    IResultSet resultSet = session.executeQuery(aSql, NumEmp, TipCol, NumCad, Data);
								try {
									if(resultSet.next()) {
										CarTra = resultSet.getInt(0);
										TotTra = TotTra + CarTra;
									  }

								  }
								  finally {
									  session.close();
								  }

								
								//TotTra = TotTra + CarTra;
								TotPre = TotPre + CarPre;
								DiaUti++;
							}
						}
						
						dDatAux = dDatAux.plusDays(1);
					}
					
					
					//Buscar Quantidade de Horas que deveria ser trabalhada na Semana
					if(DiaUti > 0) {
						
						TotSem = (TotPre - (CmpDia * DiaUti));
						//CxApuracao.setHorSit(989, TotTra);
						
						if(TotTra > TotSem) {
							//Gerar como Extra no Feriado o que trabalhou a mais
							CxApuracao.setHorSit(NorFer, (CxApuracao.getHorSit(NorFer) + (TotTra - TotSem)));
						}else
						if((TotTra < TotSem) && (TotSem > 0)){
							CxApuracao.setHorSit(SitAtr, TotSem - TotTra);
						}
						
					}
					
				}
				
				
			}

	}//Fim CmpDia > 0
	
}

public void FeriadoCompensado2 (int CodEsc, int NumEmp, int TipCol, int NumCad, int NorFer, int SitAtr, int SitSaA){
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	ContextoGeralRH CxGeral = getContainer().getContextoGeral();
	//ISeparacaoHoras separacaoHoras = CxApuracao.getHorasPrevistas(iCodHor);
	List <HistoricoAfastamento> listaAfastamentos = CxApuracao.getHistoricosAfastamento();
	int TemAfa = listaAfastamentos.size();
	int CodAfa = 0;
	if(TemAfa > 0) {
		CodAfa = listaAfastamentos.get(0).getSitAfa(); 
	}
	
	LocalDate dDatPro = CxApuracao.getData();
	LocalDate dDatSab = CxApuracao.getData();
	int HorPre = CxApuracao.getHorarioPrevisto(dDatPro);
	int CodHor = CxApuracao.getHorario().getCodigo();
	int DiaSem = dDatPro.getDayOfWeek().ordinal() + 1;
	long CarSem = CxApuracao.getEscala().getHorasSemanal();
	long HorCom = 0;
	//LocalDate dDatAdm = CxApuracao.getColaborador().getDatAdm();
	LocalDate dDatAdm = CxGeral.getColaborador(CxApuracao.getColaborador().getNumEmp(), CxApuracao.getColaborador().getTipCol(), CxApuracao.getColaborador().getNumCad()).getDatAdm();
	LocalDate dDatAux = CxApuracao.getData();
	int CmpDia = 0;
	int CarTra = 0; //Carga Horária Trabalhada
	int CarNao = 0; //Carga Horária Não Trabalhada (Abono/Afastamento)
	int CarPre = 0; //Carga Horária Prevista
	int TotPre = 0;
	int TotNao = 0;
	int HorCmp = 0; //Horas a Serem Compensadas
	String aSql = "";
	String Data = "";
	
	int DiaUti = 0;
	int DiaNao = 0;
	
	int TotTra = 0;
	int TotSem = 0;
	Feriado fer = null;
	
	//Buscar a quantidade de horas a serem compensadas diariamente
	IEntitySession entitySession = EntitySessionProvider.getSession();
	MappedParamProvider Param = new MappedParamProvider();
			
	Param.setParam("CodEsc",CodEsc);
	ICursor<IR006ESC> cIR006ESC = entitySession.newCursor(IR006ESC.class);
	cIR006ESC.addFilter("CodEsc = :CodEsc ", Param);
	cIR006ESC.open();
	try {
		if (cIR006ESC.first()) {
			IR006ESC R006ESC = cIR006ESC.read();
				
			if (R006ESC.isLimBa1Null() ==true){
			   CmpDia = 0 ;
			} else 
				{
				CmpDia = R006ESC.getLimBa1();
				}    
		}
	} 
		finally {
			cIR006ESC.close(); 	
		}
	
	if(CmpDia > 0) {
	
		//Primeira Parte
		//Dia da Semana, verificar se o próximo sábado é Compesado e Feriado.
		//Se for, todas as horas ausencias serão "desconsideradas"
		
		if(DiaSem < 6) {
			dDatSab = dDatPro.plusDays(6-DiaSem);
			HorPre = CxApuracao.getHorarioPrevisto(dDatSab);
			fer = CxApuracao.getFeriado(dDatSab);
			if(fer != null) {
				if((HorPre == 9998) && (dDatSab.equals(fer.getData()))){
					//Se o sábado próximo for feriado e compensado, todas as ausências serão substituídas pela situação FeriadoCompensado
					if(CxApuracao.getHorSit(SitAtr) > 0) {
						CxApuracao.setHorSit(990, CxApuracao.getHorSit(SitAtr));
						CxApuracao.zeraHorasSituacao(SitAtr);
					}
				}
			}
		}else
			if(DiaSem == 6) {
				
				//Verificar se dia é compensado e feriado, para verificar se colaborador trabalhou a semana sem compensar ou compensando o feriado
				//Feriado fer = CxApuracao.getFeriado(dDatPro);
				HorPre = CxApuracao.getHorarioPrevisto(dDatPro);
				TotTra = 0;
				TotPre = 0;
				DiaUti = 0;
				DiaNao = 0;
				TotSem = 0;
				
				if((CodHor == 9997) && (HorPre == 9998)) {
					//Se Sábado for feriado e compensado, voltar desde o início da semana e verificar quantidade trabalhada dia a dia.
					dDatAux = dDatPro.minusDays(5);
					
					while(dDatAux.isBefore(dDatPro)) {
						if(dDatAdm.isAfter(dDatAux)) {
							DiaNao++;
						}else
						{
							
							HorPre = CxApuracao.getHorarioPrevisto(dDatAux);
							//CarPre = CxApuracao.getHorasPrevistas(HorPre).getTotalHoras();
							CarPre = 480; //8 Horas
							CarNao = 0;
							CarTra = 0;
							
							IDBCenter database1 = DBCenter.getInstance("vetorh");
						    ISession session1 = database1.newSession();
						    aSql = "SELECT QtdHor FROM R066SIT WHERE NumEmp = ? " 
						    		+ "                          AND TipCol = ? "  
						    		+ "                          AND NumCad = ? "  
						    		+ "                          AND DatApu = ? "  
						    		+ "                          AND EXISTS"
						    		+ "                              (SELECT 1 FROM R010TOB WHERE R066SIT.CodSit = R010TOB.CodSit AND "
						    		+ "                                                           R010TOB.CodTot = 5)";
						    
						    //String Data = new java.text.SimpleDateFormat("dd/MM/yyyy").format(dDatAux.toDate());
						    DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						    Data = dDatAux.format(fmt1);
						    
						    IResultSet resultSet1 = session1.executeQuery(aSql, NumEmp, TipCol, NumCad, Data);
							try {
								while(resultSet1.next()) {
									CarNao = resultSet1.getInt(0);
									TotNao = TotNao + CarNao;
								  }

							  }
							  finally {
								  session1.close();
							  }
							
							
							//Verificar Quantidade de Horas Trabalhadas (Normais);
							IDBCenter database = DBCenter.getInstance("vetorh");
							ISession session = database.newSession();
							aSql = "SELECT QtdHor FROM R066SIT WHERE NumEmp = ? " 
							    	+ "                          AND TipCol = ? "  
							    	+ "                          AND NumCad = ? "  
							    	+ "                          AND DatApu = ? "  
							    	+ "                          AND EXISTS"
							    	+ "                              (SELECT 1 FROM R010TOB WHERE R066SIT.CodSit = R010TOB.CodSit AND "
							    	+ "                                                           R010TOB.CodTot = 3)";
							    
							//String Data = new java.text.SimpleDateFormat("dd/MM/yyyy").format(dDatAux.toDate());
							DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
							Data = dDatAux.format(fmt);
							    
							IResultSet resultSet = session.executeQuery(aSql, NumEmp, TipCol, NumCad, Data);
							try {
								while(resultSet.next()) {
									CarTra = resultSet.getInt(0);
									TotTra = TotTra + CarTra;
								}

							}
							finally {
								session.close();
							}
							
							//TotTra = Total de Horas Trabalhadas na Semana (Horas Úteis)
							//TotNao = Total de Horas Justificadas, mas, que não podem entrar na contagem do compensado
							//TotPre = Total de Horas Prevista para Trabalho na Semana
							
							
							if((CarPre >= CarNao) || (CarNao == 0)) {
								TotPre = TotPre + (CarPre - CarNao);
							}
	
						}
						
						dDatAux = dDatAux.plusDays(1);
					}
					
					CxApuracao.setHorSit(989, TotPre);
					
					if(TotPre > 0) {
						if(TotTra > TotPre) { //Trabalhou a mais na semana e deverá gerar extra
							CxApuracao.setHorSit(NorFer, CxApuracao.getHorSit(NorFer) + (TotTra - TotPre));
						}else
							if(TotTra < TotPre) {
								CxApuracao.setHorSit(SitAtr, TotPre - TotTra);
							}
					}
					
				}
				
			}

	}//Fim CmpDia > 0
	
}

public void FeriadoCompensado3 (int CodEsc, int NumEmp, int TipCol, int NumCad, int NorFer, int SitAtr, int SitSaA){
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	ContextoGeralRH CxGeral = getContainer().getContextoGeral();
	

	LocalDate dDatPro = CxApuracao.getData();
	LocalDate dDatSab = CxApuracao.getData();
	int HorPre = CxApuracao.getHorarioPrevisto(dDatPro);
	int CodHor = CxApuracao.getHorario().getCodigo();
	int DiaSem = dDatPro.getDayOfWeek().ordinal() + 1;
	long CarSem = CxApuracao.getEscala().getHorasSemanal();
	long HorCom = 0;
	//LocalDate dDatAdm = CxApuracao.getColaborador().getDatAdm();
	LocalDate dDatAdm = CxGeral.getColaborador(CxApuracao.getColaborador().getNumEmp(), CxApuracao.getColaborador().getTipCol(), CxApuracao.getColaborador().getNumCad()).getDatAdm();
	LocalDate dDatAux = CxApuracao.getData();
	int CmpDia = 0;
	int CarTra = 0; //Carga Horária Trabalhada
	int CarNao = 0; //Carga Horária Não Trabalhada (Abono/Afastamento)
	int CarPre = 0; //Carga Horária Prevista
	int TotPre = 0;
	int TotNao = 0;
	int HorCmp = 0; //Horas a Serem Compensadas
	String aSql = "";
	String Data = "";
	
	int DiaUti = 0;
	int DiaNao = 0;
	
	int TotTra = 0;
	int TotSem = 0;
	Feriado fer = null;
	int ExtDia = 0;
	int ExtSem = 0;
	int DesSem = 0;
	int DifSem = 0;
	int QtdHor = 0;
	int CodSit = 0;
	
	//Buscar a quantidade de horas a serem compensadas diariamente
	IEntitySession entitySession = EntitySessionProvider.getSession();
	MappedParamProvider Param = new MappedParamProvider();
			
	Param.setParam("CodEsc",CodEsc);
	ICursor<IR006ESC> cIR006ESC = entitySession.newCursor(IR006ESC.class);
	cIR006ESC.addFilter("CodEsc = :CodEsc", Param);
	cIR006ESC.open();
	try {
		if (cIR006ESC.first()) {
			IR006ESC R006ESC = cIR006ESC.read();
				
			if (R006ESC.isLimBa1Null() ==true){
			   CmpDia = 0 ;
			} else 
				{
				CmpDia = R006ESC.getLimBa1();
				}    
		}
	} 
		finally {
			cIR006ESC.close(); 	
		}
	
	if(CmpDia > 0) {
	
		//Primeira Parte
		//Dia da Semana, verificar se o próximo sábado é Compesado e Feriado.
		//Se for, todas as horas ausencias serão "desconsideradas"
		
		/*if(CxApuracao.getColaborador().getNumCad() == 20004273)
			CxApuracao.setHorSit(999, DiaSem);*/
		
		if(DiaSem < 6) {
			dDatSab = dDatPro.plusDays(6-DiaSem);
			HorPre = CxApuracao.getHorarioPrevisto(dDatSab);
			fer = CxApuracao.getFeriado(dDatSab);
			if(fer != null) {
				if((HorPre == 9998) && (dDatSab.equals(fer.getData()))){
					
					CarPre = CxApuracao.getHorasPrevistas(CodHor).getTotalHoras();
					//CmpDia = 480;
					//Verifica se existe alguma situação apurada, de dia todo, que deve ser alterado para carga horária prevista
					IDBCenter database2 = DBCenter.getInstance("vetorh");
				    ISession session2 = database2.newSession();
				    aSql = "SELECT CodSit FROM R066SIT WHERE NumEmp = ? AND " +
				    		                               " TipCol = ? AND " +
				    		                               " NumCad = ? AND " +
				    		                               " DatApu = ? AND " +
				    		                               " EXISTS (SELECT 1 FROM R010TOB WHERE R066SIT.CodSit = R010TOB.CodSit AND " +
				    		                                                                   " R010TOB.CodTot = 7)";  
				    //aSql = "SELECT CodSit FROM R010TOB WHERE CodTot = 7";
				    
				    
				    //String Data = new java.text.SimpleDateFormat("dd/MM/yyyy").format(dDatAux.toDate());
				    DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				    Data = dDatPro.format(fmt1);
				    
				    IResultSet resultSet2 = session2.executeQuery(aSql, NumEmp, TipCol, NumCad, Data);
					try {
						while(resultSet2.next()) {
							CodSit = resultSet2.getInt(0);
							
							
							if((CxApuracao.getHorSit(CodSit) == CarPre) && (CxApuracao.getColaborador().getNumCad() != 20004273)) {
								CxApuracao.setHorSit(CodSit, CmpDia);
							}
						  }

					  }
					  finally {
						  session2.close();
					  }
					
					
					//Se o sábado próximo for feriado e compensado, todas as ausências serão substituídas pela situação FeriadoCompensado
					if(CxApuracao.getHorSit(SitAtr) > 0) {
						CxApuracao.setHorSit(990, CxApuracao.getHorSit(SitAtr));
						CxApuracao.zeraHorasSituacao(SitAtr);
					}
				}
			}
		}else
			if(DiaSem == 6) {
				
				//Verificar se dia é compensado e feriado, para verificar se colaborador trabalhou a semana sem compensar ou compensando o feriado
				//Feriado fer = CxApuracao.getFeriado(dDatPro);
				HorPre = CxApuracao.getHorarioPrevisto(dDatPro);
				TotTra = 0;
				TotPre = 0;
				DiaUti = 0;
				DiaNao = 0;
				TotSem = 0;
				
				if((CodHor == 9997) && (HorPre == 9998)) {
					//Se Sábado for feriado e compensado, voltar desde o início da semana e verificar quantidade trabalhada dia a dia.
					dDatAux = dDatPro.minusDays(5);
					
					while(dDatAux.isBefore(dDatPro)) {
						if(dDatAdm.isAfter(dDatAux)) {
							DiaNao++;
						}else
						{
							
							HorPre = CxApuracao.getHorarioPrevisto(dDatAux);
							TotPre = CxApuracao.getHorasPrevistas(HorPre).getTotalHoras();
							//CarPre = 480; //8 Horas
							CarPre = CmpDia;
							CarNao = 0;
							CarTra = 0;
							TotTra = 0;
							TotNao = 0;
							
							IDBCenter database1 = DBCenter.getInstance("vetorh");
						    ISession session1 = database1.newSession();
						    aSql = "SELECT QtdHor FROM R066SIT WHERE NumEmp = ? " 
						    		+ "                          AND TipCol = ? "  
						    		+ "                          AND NumCad = ? "  
						    		+ "                          AND DatApu = ? "  
						    		+ "                          AND EXISTS"
						    		+ "                              (SELECT 1 FROM R010TOB WHERE R066SIT.CodSit = R010TOB.CodSit AND "
						    		+ "                                                           R010TOB.CodTot = 6)";
						    
						    //String Data = new java.text.SimpleDateFormat("dd/MM/yyyy").format(dDatAux.toDate());
						    DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						    Data = dDatAux.format(fmt1);
						    
						    IResultSet resultSet1 = session1.executeQuery(aSql, NumEmp, TipCol, NumCad, Data);
							try {
								while(resultSet1.next()) {
									CarNao = resultSet1.getInt(0);
									TotNao = TotNao + CarNao;
								  }

							  }
							  finally {
								  session1.close();
							  }
							
							
							//Verificar Quantidade de Horas Trabalhadas (Normais);
							IDBCenter database = DBCenter.getInstance("vetorh");
							ISession session = database.newSession();
							aSql = "SELECT QtdHor FROM R066SIT WHERE NumEmp = ? " 
							    	+ "                          AND TipCol = ? "  
							    	+ "                          AND NumCad = ? "  
							    	+ "                          AND DatApu = ? "  
							    	+ "                          AND EXISTS"
							    	+ "                              (SELECT 1 FROM R010TOB WHERE R066SIT.CodSit = R010TOB.CodSit AND "
							    	+ "                                                           R010TOB.CodTot = 3)";
							    
							//String Data = new java.text.SimpleDateFormat("dd/MM/yyyy").format(dDatAux.toDate());
							DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
							Data = dDatAux.format(fmt);
							    
							IResultSet resultSet = session.executeQuery(aSql, NumEmp, TipCol, NumCad, Data);
							try {
								while(resultSet.next()) {
									CarTra = resultSet.getInt(0);
									TotTra = TotTra + CarTra;
								}

							}
							finally {
								session.close();
							}
							
							if(TotNao > 0) {
								TotNao = (TotNao - (TotPre - CarPre));
							}
							
							//TotTra = Total de Horas Trabalhadas na Semana (Horas Úteis)
							//TotNao = Total de Horas Justificadas, mas, que não podem entrar na contagem do compensado
							//TotPre = Total de Horas Prevista para Trabalho na Semana
							
							if(TotTra >= CarPre)
								ExtSem = ExtSem + (TotTra - CarPre);
							else
								if((TotTra < CarPre) && (TotNao > 0)) {
									ExtSem = ExtSem - TotNao;
								}
							
							
						}
						
						dDatAux = dDatAux.plusDays(1);
					}
					
					//CxApuracao.setHorSit(989, ExtSem);
					
					if(ExtSem > 0) {
						CxApuracao.setHorSit(NorFer, CxApuracao.getHorSit(NorFer) + ExtSem);
					}else
						if(ExtSem < 0) {
							ExtSem = ExtSem * (-1);
							CxApuracao.setHorSit(SitAtr,ExtSem);
						}
					
					/*if((TotNao > 0) && (ExtSem > TotNao)){
						ExtSem = ExtSem - TotNao;
						CxApuracao.setHorSit(NorFer, CxApuracao.getHorSit(NorFer) + ExtSem);
					}else
						if((TotNao > 0) && (ExtSem < TotNao)) {
							ExtSem = TotNao - ExtSem;
							CxApuracao.setHorSit(SitAtr,ExtSem);
						}else
							if((TotNao == 0) && (ExtSem > 0)) {
								CxApuracao.setHorSit(NorFer, CxApuracao.getHorSit(NorFer) + ExtSem);
							}*/
								
					
				}
				
			}

	}//Fim CmpDia > 0
	
}





public void BancoHorasSemanal(int NumEmp, int TipCol, int NumCad, int QtdMar, int SitTra, int SitTraCmp, int NorAnt, int NorAntCmp, int SepAnt, int SepAntCmp, int SemNor, int SemNorCmp) {
	//Verificar a quantidade de horas trabalhadas semanais (Horas Normais + Banco de Horas), caso ultrapasse o limite, alterar para horas extras
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
    ContextoGeralRH CxGeral = getContainer().getContextoGeral();
    
    LocalDate DatPro = CxApuracao.getData();
    LocalDate IniSem = null;
    LocalDate FimSem = null;
    
    //Buscar se existe parametrização no banco de Horas semanal
    List<IContaBH> ConBhr = CxGeral.buscaContas(NumEmp, TipCol, NumCad, DatPro);
    if(ConBhr.size() > 0) {
    	
    	//Código do Banco de Horas
    	int CodBhr = ConBhr.get(0).getCodBhr();
    	
    	//Buscar se existe parametrização de Saldo Semanal no banco
    	if(CodBhr > 0) {
    		int QtdPer = CxGeral.getBancoHoras(CodBhr).getPeriodos().size();
    		QtdPer = QtdPer - 1;
    		long SemCre = CxGeral.getBancoHoras(CodBhr).getPeriodos().get(QtdPer).getLimiteCredito().getLimiteSemanal();
    		
    		if(SemCre > 0) { //Entrar na Regra somente se existe limite semanal configurado
    	
    			//Montar a Semana de Segunda a Domingo
    			int DiaSem = DatPro.getDayOfWeek().ordinal();
    			DiaSem = DiaSem - 1;
    		
    			IniSem = DatPro.minusDays(DiaSem);
    		
    			//Verificar quantidade de Horas Normais + Banco de Horas ocorreu durante a semana
    			int TotHor = CxApuracao.getTotalSituacoes(4, IniSem, DatPro);
    			
    			if((TotHor > SemCre) && (QtdMar > 0) && (NorAnt != SemNor)){ //Total de Horas Realizadas é maior que o total da semana
    				int HorExc = (int) (TotHor - SemCre);	
    				//Substitui situação Diurna
    				if((CxApuracao.getHorSit(NorAnt) >= HorExc) && (HorExc > 0)){
    					CxApuracao.setHorSit(SemNor, HorExc);
    					CxApuracao.setHorSit(NorAnt, CxApuracao.getHorSit(NorAnt) - HorExc);
    					HorExc = 0;
    				}else
    					if((CxApuracao.getHorSit(NorAnt) < HorExc) && (CxApuracao.getHorSit(NorAnt) > 0) && (HorExc > 0)){
    						CxApuracao.setHorSit(SemNor, CxApuracao.getHorSit(NorAnt));
    						HorExc = HorExc - CxApuracao.getHorSit(NorAnt);
    						CxApuracao.zeraHorasSituacao(NorAnt);
    					}
    			
    				//Substitui situação Noturna
    				if((CxApuracao.getHorSit(NorAntCmp) >= HorExc) && (HorExc > 0)){
    					CxApuracao.setHorSit(SemNorCmp, HorExc);
    					CxApuracao.setHorSit(NorAntCmp, CxApuracao.getHorSit(NorAntCmp) - HorExc);
    					HorExc = 0;
    				}else
    					if((CxApuracao.getHorSit(NorAntCmp) < HorExc) && (CxApuracao.getHorSit(NorAntCmp) > 0) && (HorExc > 0)){
    						CxApuracao.setHorSit(SemNorCmp, CxApuracao.getHorSit(NorAntCmp));
    						HorExc = HorExc - CxApuracao.getHorSit(NorAntCmp);
    						CxApuracao.zeraHorasSituacao(NorAntCmp);
    					}
    			
    			}
    		}	
    	}
    }
	
	
	
}

public void DecimoQuartoDiaTrabalhado12x36(int NumEmp, int TipCol, int NumCad, int NorCom, int NorComCmp, int SepCom, int SepComCmp, int LimCom, int SitTra, int SitTraCmp, int NorAnt, int NorAntCmp, int SepAnt, int SepAntCmp, int SitAtr){
	/*No Décimo Quarto dia trabalhado, se for um dia normal de trabalho, deverá ser pago como um dia compensado, ou seja, todas as horas normais serão substituídas por horas extras e as ausências excluídas*/
	
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
    ContextoGeralRH CxGeral = getContainer().getContextoGeral();
    
    LocalDate DatApu = CxApuracao.getData();
    LocalDate IniApu = null;
	LocalDate DatAux = null;
	LocalDate DatAdm = CxApuracao.getColaborador(NumEmp, TipCol, NumCad).getDatAdm();
	String ExcPon = "N";
	int UsoMar = 2;
    
    //Buscar Data Inicial do Cálculo
    IEntitySession entitySession = EntitySessionProvider.getSession();
	MappedParamProvider Param = new MappedParamProvider();
	Param.setParam("NumEmp",NumEmp);
	Param.setParam("DatApu",DatApu);
	ICursor<IR044CAL> cR044CAL = entitySession.newCursor(IR044CAL.class);
	cR044CAL.addFilter("NumEmp = :NumEmp ", Param);
	cR044CAL.addFilter("IniApu <= :DatApu ", Param);
	cR044CAL.addFilter("FimApu >= :DatApu ", Param);
	cR044CAL.open();
	try {
		if (cR044CAL.first()) {
			IR044CAL  R044CAL = cR044CAL.read();
			
			IniApu = R044CAL.getIniApu();
		}
				
	}
		finally {
			cR044CAL.close();
		}
    
	int CodHor = 0;
	int DiaTra = 0;
	DatAux = IniApu;
	
	//Se data de admissão for maior que data inicial do cálculo
	if(DatAdm.isAfter(IniApu)){
		DatAux = DatAdm;
	}
		
	//No 14° Dia Trabalhado, dentro do período, será apurado todo o trabalho realizado como horas extras
	while(DatAux.isBefore(DatApu)){
		
		//Contar quantos dias de trabalho ocorreu no período (Procurar por horários menores que 9996
		CodHor = CxGeral.getHorarioPrevistoColaborador(NumEmp, TipCol, NumCad, DatAux).getCodigo();
		if(CodHor < 9996){
			//Buscar se existem marcações no dia
			MappedParamProvider Param2 = new MappedParamProvider();
			Param2.setParam("NumEmp",NumEmp);
			Param2.setParam("TipCol",TipCol);
			Param2.setParam("NumCad",NumCad);
			Param2.setParam("DatApu",DatAux);
			Param2.setParam("ExcPon",ExcPon);
			Param2.setParam("UsoMar",UsoMar);
			ICursor<IR070ACC> cR070ACC = entitySession.newCursor(IR070ACC.class);
			cR070ACC.addFilter("NumEmp = :NumEmp ", Param2);
			cR070ACC.addFilter("TipCol = :TipCol ", Param2);
			cR070ACC.addFilter("NumCad = :NumCad ", Param2);
			cR070ACC.addFilter("ExcPon = :ExcPon ", Param2);
			cR070ACC.addFilter("UsoMar = :UsoMar ", Param2);
			cR070ACC.addFilter("DatApu = :DatApu ", Param2);
			cR070ACC.open();
			try {
				if (cR070ACC.first()) {
					IR070ACC  R070ACC = cR070ACC.read();
					DiaTra++;
				}
						
			}
				finally {
					cR070ACC.close();
				}			
			
		}
		DatAux = DatAux.plusDays(1);
	}
	
	//Dia Apurado é o 14° Dia Trabalhado
	if(DiaTra >= 13){

		//Zera Horas Atrasos
		if(CxApuracao.getHorSit(SitAtr) > 0){
			CxApuracao.zeraHorasSituacao(SitAtr);
		}
		
		//Transformar as Horas Trabalhadas em Horas Extras - Conforme Situação Cadastrada nas Definições de Situações
		if((CxApuracao.getHorSit(SitTra) > 0) && (NorCom > 0)){
			CxApuracao.setHorSit(NorCom, CxApuracao.getHorSit(NorCom) + CxApuracao.getHorSit(SitTra));
			CxApuracao.zeraHorasSituacao(SitTra);
		}
		
		if((CxApuracao.getHorSit(SitTraCmp) > 0) && (NorComCmp > 0)){
			CxApuracao.setHorSit(NorComCmp, CxApuracao.getHorSit(NorComCmp) + CxApuracao.getHorSit(SitTraCmp));
			CxApuracao.zeraHorasSituacao(SitTraCmp);
		}
		
		if((CxApuracao.getHorSit(NorAnt) > 0) && (NorCom > 0) && (NorAnt != NorCom)){
			CxApuracao.setHorSit(NorCom, CxApuracao.getHorSit(NorCom) + CxApuracao.getHorSit(NorAnt));
			CxApuracao.zeraHorasSituacao(NorAnt);
		}
		
		if((CxApuracao.getHorSit(NorAntCmp) > 0) && (NorComCmp > 0) && (NorAntCmp != NorComCmp)){
			CxApuracao.setHorSit(NorComCmp, CxApuracao.getHorSit(NorComCmp) + CxApuracao.getHorSit(NorAntCmp));
			CxApuracao.zeraHorasSituacao(NorAntCmp);
		}
		
		if((CxApuracao.getHorSit(SepAnt) > 0) && (NorCom > 0) && (SepAnt != NorCom)){
			CxApuracao.setHorSit(NorCom, CxApuracao.getHorSit(NorCom) + CxApuracao.getHorSit(SepAnt));
			CxApuracao.zeraHorasSituacao(SepAnt);
		}
		
		if((CxApuracao.getHorSit(SepAntCmp) > 0) && (NorComCmp > 0) && (SepAntCmp != NorComCmp)){
			CxApuracao.setHorSit(NorComCmp, CxApuracao.getHorSit(NorComCmp) + CxApuracao.getHorSit(SepAntCmp));
			CxApuracao.zeraHorasSituacao(SepAntCmp);
		}
		
		
		//Alterar situação de extras, caso, exista separação de horas no compensado
		if(LimCom > 0) {
			
			//Total de Horas Extras Realizadas no Dia
			int TotExt = CxApuracao.getHorSit(NorCom) + CxApuracao.getHorSit(NorComCmp);
			if((TotExt > LimCom) && (SepCom > 0) && (SepComCmp > 0) && (SepCom != NorCom)){
				int HorExc = TotExt - LimCom;
				
				//Substitui situação Diurna
				if((CxApuracao.getHorSit(NorCom) >= HorExc) && (HorExc > 0)){
					CxApuracao.setHorSit(SepCom, HorExc);
					CxApuracao.setHorSit(NorCom, CxApuracao.getHorSit(NorCom) - HorExc);
					HorExc = 0;
				}else
					if((CxApuracao.getHorSit(NorCom) < HorExc) && (CxApuracao.getHorSit(NorCom) > 0) && (HorExc > 0)){
						CxApuracao.setHorSit(SepCom, CxApuracao.getHorSit(NorCom));
						HorExc = HorExc - CxApuracao.getHorSit(NorCom);
						CxApuracao.zeraHorasSituacao(NorCom);
					}
				
				//Substitui situação noturna
				if((CxApuracao.getHorSit(NorComCmp) >= HorExc) && (HorExc > 0)){
					CxApuracao.setHorSit(SepComCmp, HorExc);
					CxApuracao.setHorSit(NorComCmp, CxApuracao.getHorSit(NorComCmp) - HorExc);
					HorExc = 0;
				}else
					if((CxApuracao.getHorSit(NorComCmp) < HorExc) && (CxApuracao.getHorSit(NorComCmp) > 0) && (HorExc > 0)){
						CxApuracao.setHorSit(SepComCmp, CxApuracao.getHorSit(NorComCmp));
						HorExc = HorExc - CxApuracao.getHorSit(NorComCmp);
						CxApuracao.zeraHorasSituacao(NorComCmp);
					}
			}
		}
	}
				
}

public int CalculaQtdMinutos ( LocalDate dDatMar1, int HorIni , LocalDate dDatMar12 , int HorFim ){
	int Resultado = 0 ;
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	if (dDatMar1.isBefore(dDatMar12)) {
		HorFim = HorFim + 1440; 	
	}
	
	Resultado = HorFim - HorIni;
	return Resultado ;
	
	
	
}

public int[] DiferencaMarcacaoDiurnoNoturno (int nMar1, LocalDate dDatMar1, int nMar2, LocalDate dDatMar2, int IniNot, int FimNot){
	
	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	int iQtdMinDiu1 = 0;
	int iQtdMinNot1 = 0;
	int iQtdMinDiu2 = 0;
	int iQtdMinNot2 = 0;
	int iQtdDiu = 0;
	int iQtdNot = 0;
	int iQtdMar = CxApuracao.getMarcacoesRealizadas(true).size();

  //Ambas as Marca  es dentro do hor rio Diurno//
  if(((nMar1 >= FimNot) && (nMar1 <= IniNot)) && ((nMar2 >= FimNot) && (nMar2 <= IniNot))){
       
    if(dDatMar1.isBefore(dDatMar2)){	
    	iQtdMinDiu1 = CalculaQtdMinutos(dDatMar1, nMar1, dDatMar1, IniNot);
    	iQtdMinNot1 = CalculaQtdMinutos(dDatMar1, IniNot, dDatMar2, 1440);
          
    	iQtdMinNot2 = CalculaQtdMinutos(dDatMar2, 1440, dDatMar2, FimNot);
    	iQtdMinDiu2 = CalculaQtdMinutos(dDatMar2, FimNot, dDatMar2, nMar2);        
    }else
      {
    	iQtdMinDiu1 = CalculaQtdMinutos(dDatMar1, nMar1, dDatMar2, nMar2);
      }
  }else
    //Ambas as Marca  es dentro do hor rio noturno
    if(((nMar1 >= IniNot) || (nMar1 <= FimNot)) && ((nMar2 >= IniNot) || (nMar2 <= FimNot))){
    
      if(dDatMar1.isBefore(dDatMar2)){
        iQtdMinDiu1 = 0;
        iQtdMinNot1 = 0;
        iQtdMinDiu2 = 0;
        iQtdMinNot2 = 0;
            
        iQtdMinNot1 = CalculaQtdMinutos(dDatMar1, nMar1, dDatMar2, 1440);
        iQtdMinNot2 = CalculaQtdMinutos(dDatMar2, 1440, dDatMar2, nMar2);
      }else
        {
    	  iQtdMinNot1 = CalculaQtdMinutos(dDatMar1, nMar1, dDatMar2, nMar2);
        }
    }else
       //Hor rio In cio Diurno e Hor rio Final Noturno@
       if(((nMar1 >= FimNot) && (nMar1 <= IniNot))&& ((nMar2 >= IniNot) || (nMar2 <= FimNot))){
          
         if(dDatMar1.isBefore(dDatMar2)){
        	 iQtdMinDiu1 = CalculaQtdMinutos(dDatMar1, nMar1, dDatMar1, IniNot);
        	 iQtdMinNot1 = CalculaQtdMinutos(dDatMar1, IniNot, dDatMar2, 1440);
          
        	 iQtdMinNot2 = CalculaQtdMinutos(dDatMar2, 1440, dDatMar2, nMar2);
         }else
           {
        	 iQtdMinDiu1 = CalculaQtdMinutos(dDatMar1, nMar1, dDatMar1, IniNot);
        	 iQtdMinNot1 = CalculaQtdMinutos(dDatMar1, IniNot, dDatMar2, nMar2);
           }
       }else
         //Hor rio In cio Noturno e Hor rio Final Diurno
         if(((nMar1 >= IniNot) || (nMar1 <= FimNot)) && ((nMar2 <= IniNot) && (nMar2 >= FimNot))){
            
           if(dDatMar1.isBefore(dDatMar2)){
        	   iQtdMinNot1 = CalculaQtdMinutos(dDatMar1, nMar1, dDatMar2, 1440);
        	   iQtdMinNot2 = CalculaQtdMinutos(dDatMar2, 1440, dDatMar2, FimNot);
        	   iQtdMinDiu1 = CalculaQtdMinutos(dDatMar2, FimNot, dDatMar2, nMar2);
           }else
             {
        	   iQtdMinNot1 = CalculaQtdMinutos(dDatMar1, nMar1, dDatMar2, FimNot);
        	   iQtdMinDiu1 = CalculaQtdMinutos(dDatMar2, FimNot, dDatMar2, nMar2);
             }
         }

	iQtdDiu = iQtdMinDiu1 + iQtdMinDiu2;
    iQtdNot = iQtdMinNot1 + iQtdMinNot2;
    int[] retorno = new int[] {iQtdDiu,iQtdNot};
	return retorno;
}

public void MenorAprendiz(int CodEsc, int SitFal, int SitAtr, int CurApr, int ExtApr, int AusApr, int NorAnt, int CodHor){

	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	ContextoGeralRH  CxGeral =    getContainer().getContextoGeral();

	int QtdMar = CxApuracao.getMarcacoesRealizadas(true).size();
	List <HistoricoAfastamento> listaAfastamentos = CxApuracao.getHistoricosAfastamento();
	List<Compensacao> compensacoes = CxApuracao.getCompensacoes(CxApuracao.getData());
	
	int TemComp =  compensacoes.size() ; 
	int QtdAfs = listaAfastamentos.size();
	LocalDate dDatPro = CxApuracao.getData();
	
	//Altera Situação de Extra por Situação de Extra Aprendiz
    if((QtdMar > 0) && (ExtApr != 0) && (CodHor < 9996)){
    	CxApuracao.setHorSit(ExtApr, CxApuracao.getHorSit(NorAnt));
    	CxApuracao.zeraHorasSituacao(NorAnt);
    }
    
    //Altera situação de Ausencia, por situação de Ausencia Aprendiz
    if((QtdMar > 0) && (AusApr != 0) && (CodHor < 9996)){
    	CxApuracao.setHorSit(AusApr, CxApuracao.getHorSit(SitAtr));
    	CxApuracao.zeraHorasSituacao(SitAtr);
    }
    
    //Falta Integral
    if((SitFal != 15) && (CxApuracao.getHorSit(SitFal) > 0)){
    	CxApuracao.setHorSit(15, CxApuracao.getHorSit(SitFal));
    	CxApuracao.zeraHorasSituacao(SitFal);
    	SitFal = 15;
    }
    
	int FlgMenAprz = 0 ;

	int DiaSem = CxGeral.getDiaSem(CxApuracao.getData());
	DiaSem ++ ;
	
	IEntitySession entitySession = EntitySessionProvider.getSession();
	MappedParamProvider Param = new MappedParamProvider();
	
	Param.setParam("CodEsc",CodEsc);
	Param.setParam("DiaSem",DiaSem);
	Param.setParam("DatPro",dDatPro);
	
	ICursor<IMenorAprendizCustom> cMenAprdz = entitySession.newCursor(IMenorAprendizCustom.class);
	
	cMenAprdz.addFilter("USU_CodEsc = :CodEsc ", Param);
	cMenAprdz.addFilter("USU_DiaCur = :DiaSem ", Param);
	cMenAprdz.addFilter("USU_DatAlt <= :DatPro ", Param);
	
	String[] campos = new String[1];
	campos[0] = "USU_DatAlt";
	OrderDirection[] ordem = new OrderDirection[1];
	ordem[0] = OrderDirection.DESC;
	cMenAprdz.setOrder(campos, ordem);
	
	cMenAprdz.open();
	try {
		if (cMenAprdz.first()) {
			IMenorAprendizCustom TabMenorAprz = cMenAprdz.read();
			FlgMenAprz =  1 ;			
		}
		
	}
	finally {
		cMenAprdz.close();
	}
	
	if (FlgMenAprz == 1) {
		if ((CxApuracao.getHorSit(SitFal) > 0) && (QtdMar == 0) && (QtdAfs == 0) && (TemComp == 0) && (CurApr != 0))  {
			CxApuracao.setHorSit(CurApr, CxApuracao.getHorSit(SitFal));
			CxApuracao.zeraHorasSituacao(SitFal);
		}	
	}
}

public void ExtrasAusencias12x36(int SitFal, int SitAtr, int Ext1236, int Ext1236Cmp, int DSR1236, int DSR1236Cmp, int Aus1236, int NorAnt, int NorAntCmp, int NorDes, int NorDesCmp, int NorFer, int NorFerCmp, int CodHor){

	ContextoApuracao CxApuracao = getContainer().getContextoApuracao();
	ContextoGeralRH  CxGeral =    getContainer().getContextoGeral();

	int QtdMar = CxApuracao.getMarcacoesRealizadas(true).size();

	//Altera Situações
	if (QtdMar > 0){
		if((Ext1236 != 0) && (CxApuracao.getHorSit(NorAnt) > 0) && (NorAnt != Ext1236)){
			CxApuracao.setHorSit(Ext1236, CxApuracao.getHorSit(NorAnt));
			CxApuracao.zeraHorasSituacao(NorAnt);
		}
    
    	if((Ext1236Cmp != 0) && (CxApuracao.getHorSit(NorAntCmp) > 0) && (NorAntCmp != Ext1236Cmp)){
    		CxApuracao.setHorSit(Ext1236Cmp, CxApuracao.getHorSit(NorAntCmp));
    		CxApuracao.zeraHorasSituacao(NorAntCmp);
    	}
			
    	if((DSR1236 != 0) && (CxApuracao.getHorSit(NorFer) > 0) && (NorFer != DSR1236)){
			CxApuracao.setHorSit(DSR1236, CxApuracao.getHorSit(NorFer));
			CxApuracao.zeraHorasSituacao(NorFer);
		}
	    
	    if((DSR1236Cmp != 0) && (CxApuracao.getHorSit(NorFerCmp) > 0) && (NorFerCmp != DSR1236Cmp)){
	    	CxApuracao.setHorSit(DSR1236Cmp, CxApuracao.getHorSit(NorFerCmp));
	    	CxApuracao.zeraHorasSituacao(NorFerCmp);
	    }

			
		if((DSR1236 != 0) && (CxApuracao.getHorSit(NorDes) > 0) && (NorDes != DSR1236)){
			CxApuracao.setHorSit(DSR1236, CxApuracao.getHorSit(NorDes));
			CxApuracao.zeraHorasSituacao(NorDes);
		}
		    
		if((DSR1236Cmp != 0) && (CxApuracao.getHorSit(NorDesCmp) > 0) && (NorDesCmp != DSR1236Cmp)){
			CxApuracao.setHorSit(DSR1236Cmp, CxApuracao.getHorSit(NorDesCmp));
		    CxApuracao.zeraHorasSituacao(NorDesCmp);
		}
		
		//Altera situação de Ausencia, por situação de Ausência 12x36
		if((Aus1236 != 0) && (CxApuracao.getHorSit(SitAtr) > 0)){
	    	CxApuracao.setHorSit(Aus1236, CxApuracao.getHorSit(SitAtr));
	    	CxApuracao.zeraHorasSituacao(SitAtr);
	    }
		
	}
			
    //Falta Integral
    if((SitFal != 15) && (CxApuracao.getHorSit(SitFal) > 0)){
    	CxApuracao.setHorSit(15, CxApuracao.getHorSit(SitFal));
    	CxApuracao.zeraHorasSituacao(SitFal);
    	SitFal = 15;
    }
}


}
