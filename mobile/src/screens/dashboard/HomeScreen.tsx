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
  StatusBar,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { theme } from '../../styles/theme';
import { DashboardService } from '../../services/api';
import { GroupDTO, BalanceDTO } from '../../types/api';
import { StackScreenProps } from '@react-navigation/stack';
import { MainStackParamList } from '../../navigation/types';

type Props = StackScreenProps<MainStackParamList, 'MainTabs'>;

export default function HomeScreen({ navigation }: any) {
  const [balance, setBalance] = useState<BalanceDTO | null>(null);
  const [groups, setGroups] = useState<GroupDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  // Estados dos Modais
  const [newGroupModalVisible, setNewGroupModalVisible] = useState(false);
  const [joinGroupModalVisible, setJoinGroupModalVisible] = useState(false);

  // Estados dos Formulários
  const [newGroupName, setNewGroupName] = useState('');
  const [newGroupDesc, setNewGroupDesc] = useState('');
  const [joinInviteCode, setJoinInviteCode] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [actionError, setActionError] = useState<string | null>(null);

  const fetchData = useCallback(async (silent = false) => {
    if (!silent) setLoading(true);
    try {
      const [balanceData, groupsData] = await Promise.all([
        DashboardService.getBalanceSummary(),
        DashboardService.getGroups(),
      ]);
      setBalance(balanceData);
      setGroups(groupsData);
    } catch (error) {
      console.error('Erro ao buscar dados do Dashboard:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const onRefresh = () => {
    setRefreshing(true);
    fetchData(true);
  };

  // Submissão do Novo Grupo
  const handleCreateGroup = async () => {
    if (!newGroupName) {
      setActionError('O nome do grupo é obrigatório.');
      return;
    }
    setActionLoading(true);
    setActionError(null);
    try {
      const created = await DashboardService.createGroup(newGroupName, newGroupDesc);
      setNewGroupModalVisible(false);
      setNewGroupName('');
      setNewGroupDesc('');
      
      // Atualizar lista local
      await fetchData(true);
      
      // Navegar diretamente para o novo grupo
      navigation.navigate('GroupDetail', { groupId: created.id, groupName: created.name });
    } catch (err: any) {
      setActionError(err.message || 'Erro ao criar grupo.');
    } finally {
      setActionLoading(false);
    }
  };

  // Submissão do Código de Convite
  const handleJoinGroup = async () => {
    if (!joinInviteCode) {
      setActionError('O código de convite é obrigatório.');
      return;
    }
    setActionLoading(true);
    setActionError(null);
    try {
      const joined = await DashboardService.joinGroup(joinInviteCode);
      setJoinGroupModalVisible(false);
      setJoinInviteCode('');
      
      // Atualizar dados
      await fetchData(true);

      // Navegar para o grupo aderido
      navigation.navigate('GroupDetail', { groupId: joined.id, groupName: joined.name });
    } catch (err: any) {
      setActionError(err.message || 'Código de convite inválido ou erro ao aderir.');
    } finally {
      setActionLoading(false);
    }
  };

  const renderGroupItem = ({ item }: { item: GroupDTO }) => {
    const isOwed = item.userBalance > 0;
    const isOwe = item.userBalance < 0;
    const balanceColor = isOwed
      ? theme.colors.success
      : isOwe
      ? theme.colors.danger
      : theme.colors.textMuted;

    return (
      <TouchableOpacity
        style={styles.groupCard}
        activeOpacity={0.7}
        onPress={() =>
          navigation.navigate('GroupDetail', {
            groupId: item.id,
            groupName: item.name,
          })
        }
      >
        <View style={styles.groupCardHeader}>
          <View style={styles.groupIconContainer}>
            <Ionicons name="people" size={22} color={theme.colors.primaryLight} />
          </View>
          <View style={styles.groupInfo}>
            <Text style={styles.groupName} numberOfLines={1}>
              {item.name}
            </Text>
            <Text style={styles.groupMembers}>
              {item.memberCount} membros
            </Text>
          </View>
        </View>

        <View style={styles.groupCardDivider} />

        <View style={styles.groupCardFooter}>
          <Text style={styles.balanceLabel}>O teu saldo:</Text>
          <Text style={[styles.balanceValue, { color: balanceColor }]}>
            {item.userBalance === 0
              ? 'Tudo pago'
              : `${isOwed ? '+' : ''}€${Math.abs(item.userBalance).toFixed(2)}`}
          </Text>
        </View>
        <Ionicons name="chevron-forward" size={16} color={theme.colors.border} style={styles.arrowIcon} />
      </TouchableOpacity>
    );
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color={theme.colors.primary} />
        <Text style={styles.loadingText}>A carregar finanças...</Text>
      </View>
    );
  }

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" />
      
      <FlatList
        data={groups}
        keyExtractor={(item) => item.id}
        renderItem={renderGroupItem}
        contentContainerStyle={styles.listContent}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={theme.colors.primary} />
        }
        ListHeaderComponent={
          <>
            {/* Secção do Resumo Financeiro */}
            <View style={styles.summaryContainer}>
              <View style={[styles.summaryCard, styles.mainSummaryCard]}>
                <Text style={styles.summaryCardLabel}>Saldo Líquido</Text>
                <Text
                  style={[
                    styles.mainBalanceText,
                    {
                      color:
                        (balance?.netBalance ?? 0) >= 0
                          ? theme.colors.success
                          : theme.colors.danger,
                    },
                  ]}
                >
                  {(balance?.netBalance ?? 0) >= 0 ? '+' : '-'}
                  €{Math.abs(balance?.netBalance ?? 0).toFixed(2)}
                </Text>
              </View>

              <View style={styles.rowSummaryCards}>
                <View style={[styles.summaryCard, styles.subSummaryCard]}>
                  <View style={styles.subCardHeader}>
                    <Ionicons name="arrow-down-circle" size={18} color={theme.colors.danger} />
                    <Text style={styles.subCardLabel}>Deves</Text>
                  </View>
                  <Text style={[styles.subBalanceText, { color: theme.colors.danger }]}>
                    €{(balance?.oweAmount ?? 0).toFixed(2)}
                  </Text>
                </View>

                <View style={[styles.summaryCard, styles.subSummaryCard]}>
                  <View style={styles.subCardHeader}>
                    <Ionicons name="arrow-up-circle" size={18} color={theme.colors.success} />
                    <Text style={styles.subCardLabel}>Devem-te</Text>
                  </View>
                  <Text style={[styles.subBalanceText, { color: theme.colors.success }]}>
                    €{(balance?.owedAmount ?? 0).toFixed(2)}
                  </Text>
                </View>
              </View>
            </View>

            {/* Ações Rápidas */}
            <View style={styles.actionsContainer}>
              <TouchableOpacity
                style={[styles.actionButton, { backgroundColor: theme.colors.primary }]}
                activeOpacity={0.8}
                onPress={() => setNewGroupModalVisible(true)}
              >
                <Ionicons name="add" size={20} color={theme.colors.white} style={{ marginRight: 6 }} />
                <Text style={styles.actionButtonText}>Novo Grupo</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={[styles.actionButton, styles.secondaryActionButton]}
                activeOpacity={0.8}
                onPress={() => setJoinGroupModalVisible(true)}
              >
                <Ionicons name="enter-outline" size={20} color={theme.colors.text} style={{ marginRight: 6 }} />
                <Text style={styles.actionButtonText}>Aderir Código</Text>
              </TouchableOpacity>
            </View>

            {/* Título de Lista */}
            <Text style={styles.sectionTitle}>Grupos Ativos</Text>
          </>
        }
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <Ionicons name="people-outline" size={48} color={theme.colors.border} />
            <Text style={styles.emptyText}>Ainda não pertence a nenhum grupo.</Text>
            <Text style={styles.emptySubText}>Crie um novo grupo ou adira a um grupo existente.</Text>
          </View>
        }
      />

      {/* --- MODAL: NOVO GRUPO --- */}
      <Modal
        visible={newGroupModalVisible}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setNewGroupModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Criar Novo Grupo</Text>
              <TouchableOpacity onPress={() => setNewGroupModalVisible(false)}>
                <Ionicons name="close" size={24} color={theme.colors.text} />
              </TouchableOpacity>
            </View>

            {actionError && <Text style={styles.modalError}>{actionError}</Text>}

            <Text style={styles.modalLabel}>Nome do Grupo</Text>
            <TextInput
              style={styles.modalInput}
              placeholder="Ex: Viagem a Londres, Contas da Casa"
              placeholderTextColor={theme.colors.textMuted}
              value={newGroupName}
              onChangeText={setNewGroupName}
            />

            <Text style={styles.modalLabel}>Descrição (Opcional)</Text>
            <TextInput
              style={[styles.modalInput, styles.modalTextArea]}
              placeholder="Sobre o que são estas despesas..."
              placeholderTextColor={theme.colors.textMuted}
              value={newGroupDesc}
              onChangeText={setNewGroupDesc}
              multiline={true}
              numberOfLines={3}
            />

            <TouchableOpacity
              style={styles.modalSubmitButton}
              onPress={handleCreateGroup}
              disabled={actionLoading}
            >
              {actionLoading ? (
                <ActivityIndicator color={theme.colors.white} />
              ) : (
                <Text style={styles.modalSubmitButtonText}>Criar Grupo</Text>
              )}
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      {/* --- MODAL: JUNTAR A GRUPO --- */}
      <Modal
        visible={joinGroupModalVisible}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setJoinGroupModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Aderir a Grupo</Text>
              <TouchableOpacity onPress={() => setJoinGroupModalVisible(false)}>
                <Ionicons name="close" size={24} color={theme.colors.text} />
              </TouchableOpacity>
            </View>

            {actionError && <Text style={styles.modalError}>{actionError}</Text>}

            <Text style={styles.modalLabel}>Código de Convite</Text>
            <TextInput
              style={[styles.modalInput, styles.modalCodeInput]}
              placeholder="Ex: CASAT2XP"
              placeholderTextColor={theme.colors.textMuted}
              value={joinInviteCode}
              onChangeText={setJoinInviteCode}
              autoCapitalize="characters"
              autoCorrect={false}
            />
            <Text style={styles.modalHelperText}>Insira o código partilhado por um dos membros do grupo.</Text>

            <TouchableOpacity
              style={styles.modalSubmitButton}
              onPress={handleJoinGroup}
              disabled={actionLoading}
            >
              {actionLoading ? (
                <ActivityIndicator color={theme.colors.white} />
              ) : (
                <Text style={styles.modalSubmitButtonText}>Aderir ao Grupo</Text>
              )}
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
  },
  summaryContainer: {
    marginBottom: theme.spacing.lg,
  },
  summaryCard: {
    backgroundColor: theme.colors.surface,
    borderRadius: theme.borderRadius.md,
    padding: theme.spacing.md,
    borderWidth: 1,
    borderColor: theme.colors.border,
    ...theme.shadows.sm,
  },
  mainSummaryCard: {
    alignItems: 'center',
    paddingVertical: theme.spacing.lg,
    marginBottom: theme.spacing.md,
  },
  summaryCardLabel: {
    color: theme.colors.textMuted,
    fontSize: theme.typography.sizes.sm,
    fontWeight: '600',
    textTransform: 'uppercase',
    letterSpacing: 0.8,
  },
  mainBalanceText: {
    fontSize: theme.typography.sizes.title,
    fontWeight: 'bold',
    marginTop: theme.spacing.xs,
  },
  rowSummaryCards: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  subSummaryCard: {
    width: '48%',
    padding: theme.spacing.sm + 4,
  },
  subCardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: theme.spacing.xs,
  },
  subCardLabel: {
    color: theme.colors.textMuted,
    fontSize: theme.typography.sizes.xs + 1,
    fontWeight: '500',
    marginLeft: 6,
  },
  subBalanceText: {
    fontSize: theme.typography.sizes.lg,
    fontWeight: 'bold',
    marginTop: 2,
  },
  actionsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: theme.spacing.xl,
  },
  actionButton: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    height: 48,
    borderRadius: theme.borderRadius.md,
    marginHorizontal: theme.spacing.xs,
    ...theme.shadows.sm,
  },
  secondaryActionButton: {
    backgroundColor: theme.colors.surface,
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  actionButtonText: {
    color: theme.colors.text,
    fontSize: theme.typography.sizes.sm + 1,
    fontWeight: 'bold',
  },
  sectionTitle: {
    fontSize: theme.typography.sizes.lg,
    fontWeight: 'bold',
    color: theme.colors.text,
    marginBottom: theme.spacing.md,
  },
  groupCard: {
    backgroundColor: theme.colors.surface,
    borderRadius: theme.borderRadius.md,
    padding: theme.spacing.md,
    marginBottom: theme.spacing.md,
    borderWidth: 1,
    borderColor: theme.colors.border,
    position: 'relative',
    ...theme.shadows.sm,
  },
  groupCardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  groupIconContainer: {
    backgroundColor: 'rgba(139, 92, 246, 0.1)',
    width: 44,
    height: 44,
    borderRadius: theme.borderRadius.sm,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: theme.spacing.md,
  },
  groupInfo: {
    flex: 1,
    paddingRight: theme.spacing.lg,
  },
  groupName: {
    fontSize: theme.typography.sizes.md,
    fontWeight: 'bold',
    color: theme.colors.text,
  },
  groupMembers: {
    fontSize: theme.typography.sizes.xs + 1,
    color: theme.colors.textMuted,
    marginTop: 2,
  },
  groupCardDivider: {
    height: 1,
    backgroundColor: theme.colors.border,
    marginVertical: theme.spacing.sm + 2,
  },
  groupCardFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  balanceLabel: {
    fontSize: theme.typography.sizes.sm,
    color: theme.colors.textMuted,
  },
  balanceValue: {
    fontSize: theme.typography.sizes.sm + 1,
    fontWeight: 'bold',
  },
  arrowIcon: {
    position: 'absolute',
    right: theme.spacing.md,
    top: '40%',
  },
  emptyContainer: {
    alignItems: 'center',
    justifyContent: 'center',
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
  modalTextArea: {
    height: 80,
    paddingTop: theme.spacing.sm,
    textAlignVertical: 'top',
  },
  modalCodeInput: {
    fontSize: theme.typography.sizes.xl,
    fontWeight: 'bold',
    textAlign: 'center',
    letterSpacing: 4,
  },
  modalHelperText: {
    color: theme.colors.textMuted,
    fontSize: theme.typography.sizes.xs,
    marginTop: 4,
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
});
