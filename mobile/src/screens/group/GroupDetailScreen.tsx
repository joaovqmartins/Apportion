import React, { useEffect, useState, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Modal,
  TextInput,
  ActivityIndicator,
  SafeAreaView,
  RefreshControl,
  Alert,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { theme } from '../../styles/theme';
import { GroupService } from '../../services/api';
import { GroupDTO, ExpenseDTO } from '../../types/api';
import { StackScreenProps } from '@react-navigation/stack';
import { MainStackParamList } from '../../navigation/types';

type Props = StackScreenProps<MainStackParamList, 'GroupDetail'>;

export default function GroupDetailScreen({ route, navigation }: Props) {
  const { groupId, groupName } = route.params;

  const [group, setGroup] = useState<GroupDTO | null>(null);
  const [expenses, setExpenses] = useState<ExpenseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  // Modais
  const [expenseModalVisible, setExpenseModalVisible] = useState(false);
  const [memberModalVisible, setMemberModalVisible] = useState(false);
  const [inviteModalVisible, setInviteModalVisible] = useState(false);

  // Formulários
  const [expDescription, setExpDescription] = useState('');
  const [expAmount, setExpAmount] = useState('');
  const [expPayer, setExpPayer] = useState<'me' | 'other'>('me'); // 'me' = Pago por mim, 'other' = Outro membro
  const [expPayerName, setExpPayerName] = useState('Ana Martins'); // Nome padrão caso seja outro
  
  const [memberEmail, setMemberEmail] = useState('');
  
  const [actionLoading, setActionLoading] = useState(false);
  const [actionError, setActionError] = useState<string | null>(null);

  const fetchData = useCallback(async (silent = false) => {
    if (!silent) setLoading(true);
    try {
      const [details, expenseList] = await Promise.all([
        GroupService.getGroupDetails(groupId),
        GroupService.getGroupExpenses(groupId),
      ]);
      setGroup(details);
      setExpenses(expenseList);
    } catch (error) {
      console.error('Erro ao carregar detalhes do grupo:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [groupId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const onRefresh = () => {
    setRefreshing(true);
    fetchData(true);
  };

  // Enviar Nova Despesa
  const handleAddExpense = async () => {
    if (!expDescription || !expAmount) {
      setActionError('Preencha a descrição e o valor da despesa.');
      return;
    }
    const amountNum = parseFloat(expAmount.replace(',', '.'));
    if (isNaN(amountNum) || amountNum <= 0) {
      setActionError('Insira um valor numérico válido maior que zero.');
      return;
    }

    setActionLoading(true);
    setActionError(null);
    try {
      await GroupService.addExpense(groupId, {
        description: expDescription,
        amount: amountNum,
        paidById: expPayer === 'me' ? 'usr-001' : 'usr-other',
        paidByName: expPayer === 'me' ? 'Tu' : expPayerName,
      });

      setExpenseModalVisible(false);
      setExpDescription('');
      setExpAmount('');
      setExpPayer('me');

      // Recarregar dados com novos saldos calculados no mock/backend
      await fetchData(true);
    } catch (err: any) {
      setActionError(err.message || 'Erro ao adicionar despesa.');
    } finally {
      setActionLoading(false);
    }
  };

  // Enviar Adicionar Membro
  const handleAddMember = async () => {
    if (!memberEmail) {
      setActionError('O e-mail é obrigatório.');
      return;
    }
    setActionLoading(true);
    setActionError(null);
    try {
      const response = await GroupService.addMemberToGroup(groupId, memberEmail);
      if (response.success) {
        setMemberModalVisible(false);
        setMemberEmail('');
        Alert.alert('Sucesso', response.message);
        await fetchData(true);
      }
    } catch (err: any) {
      setActionError(err.message || 'Erro ao adicionar membro.');
    } finally {
      setActionLoading(false);
    }
  };

  const formatDate = (isoString: string) => {
    const date = new Date(isoString);
    const day = date.getDate().toString().padStart(2, '0');
    const months = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
    const month = months[date.getMonth()];
    return { day, month };
  };

  const renderExpenseItem = ({ item }: { item: ExpenseDTO }) => {
    const { day, month } = formatDate(item.date);
    const isPaidByMe = item.paidById === 'usr-001';

    // Para fins visuais, calcula a divisão igualmente
    const portion = group ? item.amount / group.memberCount : 0;

    return (
      <View style={styles.expenseCard}>
        {/* Bloco de Data */}
        <View style={styles.dateBlock}>
          <Text style={styles.dateDay}>{day}</Text>
          <Text style={styles.dateMonth}>{month}</Text>
        </View>

        {/* Info da Despesa */}
        <View style={styles.expenseInfo}>
          <Text style={styles.expenseDesc} numberOfLines={1}>
            {item.description}
          </Text>
          <Text style={styles.expensePayer}>
            Pago por <Text style={{ fontWeight: '600' }}>{item.paidByName}</Text>
          </Text>
        </View>

        {/* Valores */}
        <View style={styles.expenseValues}>
          <Text style={styles.totalAmount}>€{item.amount.toFixed(2)}</Text>
          {isPaidByMe ? (
            <Text style={[styles.shareText, { color: theme.colors.success }]}>
              recebes €{(item.amount - portion).toFixed(2)}
            </Text>
          ) : (
            <Text style={[styles.shareText, { color: theme.colors.danger }]}>
              deves €{portion.toFixed(2)}
            </Text>
          )}
        </View>
      </View>
    );
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color={theme.colors.primary} />
        <Text style={styles.loadingText}>A carregar histórico...</Text>
      </View>
    );
  }

  const userBalance = group?.userBalance ?? 0;
  const isOwed = userBalance > 0;
  const isOwe = userBalance < 0;
  const balanceColor = isOwed
    ? theme.colors.success
    : isOwe
    ? theme.colors.danger
    : theme.colors.textMuted;

  return (
    <SafeAreaView style={styles.safeArea}>
      
      {/* Listagem com Cabeçalho Customizado */}
      <FlatList
        data={expenses}
        keyExtractor={(item) => item.id}
        renderItem={renderExpenseItem}
        contentContainerStyle={styles.listContent}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={theme.colors.primary} />
        }
        ListHeaderComponent={
          <>
            {/* Banner de Saldo Global no Grupo */}
            <View style={[styles.balanceBanner, { borderColor: theme.colors.border }]}>
              <Text style={styles.bannerLabel}>O teu saldo neste grupo</Text>
              <Text style={[styles.bannerBalance, { color: balanceColor }]}>
                {userBalance === 0
                  ? 'Estás equilibrado'
                  : `${isOwed ? 'Devem-te' : 'Deves'} €${Math.abs(userBalance).toFixed(2)}`}
              </Text>
              <Text style={styles.bannerSubText}>
                Grupo com {group?.memberCount} membros ativos
              </Text>
            </View>

            {/* Ações de Grupo */}
            <View style={styles.actionsRow}>
              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => setMemberModalVisible(true)}
              >
                <Ionicons name="person-add-outline" size={18} color={theme.colors.primaryLight} />
                <Text style={styles.actionBtnText}>Membro</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.actionBtn}
                onPress={() => setInviteModalVisible(true)}
              >
                <Ionicons name="qr-code-outline" size={18} color={theme.colors.primaryLight} />
                <Text style={styles.actionBtnText}>Código</Text>
              </TouchableOpacity>
            </View>

            {/* Título de Histórico */}
            <Text style={styles.sectionTitle}>Histórico de Despesas</Text>
          </>
        }
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <Ionicons name="receipt-outline" size={48} color={theme.colors.border} />
            <Text style={styles.emptyText}>Sem despesas registadas</Text>
            <Text style={styles.emptySubText}>
              Clica no botão '+' abaixo para adicionar a primeira despesa do grupo.
            </Text>
          </View>
        }
      />

      {/* FLOATING ACTION BUTTON (FAB) */}
      <TouchableOpacity
        style={styles.fab}
        activeOpacity={0.85}
        onPress={() => setExpenseModalVisible(true)}
      >
        <Ionicons name="add" size={30} color={theme.colors.white} />
      </TouchableOpacity>

      {/* --- MODAL: NOVA DESPESA --- */}
      <Modal
        visible={expenseModalVisible}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setExpenseModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Adicionar Despesa</Text>
              <TouchableOpacity onPress={() => setExpenseModalVisible(false)}>
                <Ionicons name="close" size={24} color={theme.colors.text} />
              </TouchableOpacity>
            </View>

            {actionError && <Text style={styles.modalError}>{actionError}</Text>}

            <Text style={styles.modalLabel}>Valor (€)</Text>
            <TextInput
              style={[styles.modalInput, styles.modalAmountInput]}
              placeholder="0.00"
              placeholderTextColor={theme.colors.textMuted}
              value={expAmount}
              onChangeText={setExpAmount}
              keyboardType="numeric"
              autoFocus={true}
            />

            <Text style={styles.modalLabel}>Descrição</Text>
            <TextInput
              style={styles.modalInput}
              placeholder="Ex: Jantar, Gasóleo, Compras"
              placeholderTextColor={theme.colors.textMuted}
              value={expDescription}
              onChangeText={setExpDescription}
            />

            <Text style={styles.modalLabel}>Quem pagou?</Text>
            <View style={styles.payerSelectorRow}>
              <TouchableOpacity
                style={[
                  styles.selectorBtn,
                  expPayer === 'me' && styles.selectorBtnActive,
                ]}
                onPress={() => setExpPayer('me')}
              >
                <Text
                  style={[
                    styles.selectorBtnText,
                    expPayer === 'me' && styles.selectorBtnTextActive,
                  ]}
                >
                  Paguei eu (Tu)
                </Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={[
                  styles.selectorBtn,
                  expPayer === 'other' && styles.selectorBtnActive,
                ]}
                onPress={() => setExpPayer('other')}
              >
                <Text
                  style={[
                    styles.selectorBtnText,
                    expPayer === 'other' && styles.selectorBtnTextActive,
                  ]}
                >
                  Outro Membro
                </Text>
              </TouchableOpacity>
            </View>

            {expPayer === 'other' && (
              <View style={{ marginTop: theme.spacing.sm }}>
                <Text style={styles.modalLabel}>Nome do Payer</Text>
                <TextInput
                  style={styles.modalInput}
                  placeholder="Nome de quem pagou"
                  placeholderTextColor={theme.colors.textMuted}
                  value={expPayerName}
                  onChangeText={setExpPayerName}
                />
              </View>
            )}

            <Text style={styles.helperSplitText}>
              * A despesa será dividida em partes iguais entre todos os membros do grupo. O cálculo detalhado é gerido pelo backend em Java.
            </Text>

            <TouchableOpacity
              style={styles.modalSubmitButton}
              onPress={handleAddExpense}
              disabled={actionLoading}
            >
              {actionLoading ? (
                <ActivityIndicator color={theme.colors.white} />
              ) : (
                <Text style={styles.modalSubmitButtonText}>Salvar Despesa</Text>
              )}
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      {/* --- MODAL: ADICIONAR MEMBRO --- */}
      <Modal
        visible={memberModalVisible}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setMemberModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Adicionar Membro</Text>
              <TouchableOpacity onPress={() => setMemberModalVisible(false)}>
                <Ionicons name="close" size={24} color={theme.colors.text} />
              </TouchableOpacity>
            </View>

            {actionError && <Text style={styles.modalError}>{actionError}</Text>}

            <Text style={styles.modalLabel}>Endereço de E-mail</Text>
            <TextInput
              style={styles.modalInput}
              placeholder="membro@email.com"
              placeholderTextColor={theme.colors.textMuted}
              value={memberEmail}
              onChangeText={setMemberEmail}
              keyboardType="email-address"
              autoCapitalize="none"
            />

            <TouchableOpacity
              style={styles.modalSubmitButton}
              onPress={handleAddMember}
              disabled={actionLoading}
            >
              {actionLoading ? (
                <ActivityIndicator color={theme.colors.white} />
              ) : (
                <Text style={styles.modalSubmitButtonText}>Adicionar ao Grupo</Text>
              )}
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      {/* --- MODAL: CÓDIGO DE CONVITE --- */}
      <Modal
        visible={inviteModalVisible}
        transparent={true}
        animationType="fade"
        onRequestClose={() => setInviteModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={[styles.modalContent, { alignItems: 'center' }]}>
            <Ionicons name="qr-code" size={60} color={theme.colors.primary} style={{ marginBottom: theme.spacing.md }} />
            <Text style={styles.modalTitle}>Código de Convite</Text>
            <Text style={styles.inviteDescription}>
              Partilha este código com as pessoas que queres convidar para este grupo.
            </Text>

            <View style={styles.inviteCodeContainer}>
              <Text style={styles.inviteCodeText}>{group?.inviteCode}</Text>
            </View>

            <TouchableOpacity
              style={[styles.modalSubmitButton, { width: '100%', marginTop: theme.spacing.lg }]}
              onPress={() => setInviteModalVisible(false)}
            >
              <Text style={styles.modalSubmitButtonText}>Fechar</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: theme.colors.background,
  },
  loadingContainer: {
    flex: 1,
    backgroundColor: theme.colors.background,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    color: theme.colors.textMuted,
    marginTop: theme.spacing.md,
    fontSize: theme.typography.sizes.md,
  },
  listContent: {
    padding: theme.spacing.lg,
    paddingBottom: 100, // Espaço para o FAB não cobrir itens
  },
  balanceBanner: {
    backgroundColor: theme.colors.surface,
    borderWidth: 1,
    borderRadius: theme.borderRadius.md,
    padding: theme.spacing.lg,
    alignItems: 'center',
    marginBottom: theme.spacing.lg,
    ...theme.shadows.sm,
  },
  bannerLabel: {
    fontSize: theme.typography.sizes.xs + 1,
    color: theme.colors.textMuted,
    textTransform: 'uppercase',
    letterSpacing: 0.8,
    fontWeight: '600',
  },
  bannerBalance: {
    fontSize: theme.typography.sizes.xxl,
    fontWeight: 'bold',
    marginVertical: theme.spacing.xs,
  },
  bannerSubText: {
    fontSize: theme.typography.sizes.xs,
    color: theme.colors.textMuted,
  },
  actionsRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: theme.spacing.xl,
  },
  actionBtn: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: theme.colors.surface,
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderRadius: theme.borderRadius.md,
    height: 44,
    marginHorizontal: theme.spacing.xs,
  },
  actionBtnText: {
    color: theme.colors.text,
    fontSize: theme.typography.sizes.sm,
    fontWeight: '600',
    marginLeft: 6,
  },
  sectionTitle: {
    fontSize: theme.typography.sizes.md + 2,
    fontWeight: 'bold',
    color: theme.colors.text,
    marginBottom: theme.spacing.md,
  },
  expenseCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: theme.colors.surface,
    borderRadius: theme.borderRadius.md,
    padding: theme.spacing.md,
    marginBottom: theme.spacing.sm + 2,
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  dateBlock: {
    backgroundColor: 'rgba(255,255,255,0.05)',
    borderRadius: theme.borderRadius.sm,
    paddingVertical: theme.spacing.xs,
    paddingHorizontal: theme.spacing.sm,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: theme.spacing.md,
    minWidth: 46,
  },
  dateDay: {
    fontSize: theme.typography.sizes.md,
    fontWeight: 'bold',
    color: theme.colors.text,
  },
  dateMonth: {
    fontSize: theme.typography.sizes.xs - 2,
    textTransform: 'uppercase',
    color: theme.colors.textMuted,
    marginTop: 1,
    fontWeight: 'bold',
  },
  expenseInfo: {
    flex: 1,
    paddingRight: theme.spacing.sm,
  },
  expenseDesc: {
    fontSize: theme.typography.sizes.md - 1,
    fontWeight: 'bold',
    color: theme.colors.text,
  },
  expensePayer: {
    fontSize: theme.typography.sizes.xs + 1,
    color: theme.colors.textMuted,
    marginTop: 2,
  },
  expenseValues: {
    alignItems: 'flex-end',
  },
  totalAmount: {
    fontSize: theme.typography.sizes.md - 1,
    fontWeight: 'bold',
    color: theme.colors.text,
  },
  shareText: {
    fontSize: theme.typography.sizes.xs,
    marginTop: 3,
    fontWeight: '500',
  },
  emptyContainer: {
    alignItems: 'center',
    paddingVertical: theme.spacing.xxl,
  },
  emptyText: {
    fontSize: theme.typography.sizes.md,
    fontWeight: '600',
    color: theme.colors.text,
    marginTop: theme.spacing.md,
  },
  emptySubText: {
    fontSize: theme.typography.sizes.sm,
    color: theme.colors.textMuted,
    textAlign: 'center',
    marginTop: theme.spacing.xs,
    paddingHorizontal: theme.spacing.xl,
  },
  fab: {
    position: 'absolute',
    bottom: theme.spacing.lg + 8,
    right: theme.spacing.lg,
    backgroundColor: theme.colors.primary,
    width: 60,
    height: 60,
    borderRadius: theme.borderRadius.full,
    justifyContent: 'center',
    alignItems: 'center',
    ...theme.shadows.md,
  },
  // ESTILOS DOS MODAIS
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    justifyContent: 'center',
    padding: theme.spacing.lg,
  },
  modalContent: {
    backgroundColor: theme.colors.surface,
    borderRadius: theme.borderRadius.lg,
    padding: theme.spacing.lg,
    borderWidth: 1,
    borderColor: theme.colors.border,
    ...theme.shadows.lg,
  },
  modalHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing.lg,
  },
  modalTitle: {
    fontSize: theme.typography.sizes.lg + 1,
    fontWeight: 'bold',
    color: theme.colors.text,
  },
  modalError: {
    color: theme.colors.danger,
    backgroundColor: 'rgba(244, 63, 94, 0.1)',
    padding: theme.spacing.sm,
    borderRadius: theme.borderRadius.sm,
    marginBottom: theme.spacing.md,
    fontSize: theme.typography.sizes.sm,
  },
  modalLabel: {
    fontSize: theme.typography.sizes.sm,
    fontWeight: '600',
    color: theme.colors.text,
    marginBottom: theme.spacing.xs,
    marginTop: theme.spacing.sm,
  },
  modalInput: {
    backgroundColor: theme.colors.background,
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderRadius: theme.borderRadius.sm,
    paddingHorizontal: theme.spacing.md,
    height: 48,
    color: theme.colors.text,
    fontSize: theme.typography.sizes.md,
  },
  modalAmountInput: {
    fontSize: theme.typography.sizes.xxl,
    fontWeight: 'bold',
    height: 60,
    textAlign: 'center',
  },
  payerSelectorRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: theme.spacing.xs,
  },
  selectorBtn: {
    flex: 1,
    backgroundColor: theme.colors.background,
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderRadius: theme.borderRadius.sm,
    height: 44,
    justifyContent: 'center',
    alignItems: 'center',
    marginHorizontal: 4,
  },
  selectorBtnActive: {
    backgroundColor: 'rgba(139, 92, 246, 0.15)',
    borderColor: theme.colors.primary,
  },
  selectorBtnText: {
    color: theme.colors.textMuted,
    fontSize: theme.typography.sizes.sm,
    fontWeight: '500',
  },
  selectorBtnTextActive: {
    color: theme.colors.primaryLight,
    fontWeight: 'bold',
  },
  helperSplitText: {
    color: theme.colors.textMuted,
    fontSize: theme.typography.sizes.xs - 1,
    lineHeight: 16,
    marginTop: theme.spacing.md,
    fontStyle: 'italic',
  },
  modalSubmitButton: {
    backgroundColor: theme.colors.primary,
    height: 50,
    borderRadius: theme.borderRadius.sm,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: theme.spacing.xl,
  },
  modalSubmitButtonText: {
    color: theme.colors.white,
    fontSize: theme.typography.sizes.md,
    fontWeight: 'bold',
  },
  inviteDescription: {
    color: theme.colors.textMuted,
    fontSize: theme.typography.sizes.sm,
    textAlign: 'center',
    marginTop: theme.spacing.xs,
    paddingHorizontal: theme.spacing.sm,
  },
  inviteCodeContainer: {
    backgroundColor: theme.colors.background,
    borderWidth: 1.5,
    borderColor: theme.colors.primary,
    borderStyle: 'dashed',
    borderRadius: theme.borderRadius.md,
    paddingVertical: theme.spacing.md,
    paddingHorizontal: theme.spacing.xl,
    marginTop: theme.spacing.lg,
  },
  inviteCodeText: {
    color: theme.colors.primaryLight,
    fontSize: theme.typography.sizes.xl + 4,
    fontWeight: 'bold',
    letterSpacing: 2,
  },
});
