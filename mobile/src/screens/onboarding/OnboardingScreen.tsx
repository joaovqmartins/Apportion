import React, { useRef, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Dimensions,
  TouchableOpacity,
  FlatList,
  Animated,
  StatusBar,
  ViewToken,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { theme } from '../../styles/theme';

const { width, height } = Dimensions.get('window');

interface OnboardingSlide {
  id: string;
  icon: keyof typeof Ionicons.glyphMap;
  iconColor: string;
  iconBg: string;
  accentColor: string;
  title: string;
  subtitle: string;
  highlight: string; // palavra em destaque no título
  decorShape: 'circle' | 'triangle' | 'diamond';
}

const slides: OnboardingSlide[] = [
  {
    id: '1',
    icon: 'people',
    iconColor: '#8B5CF6',
    iconBg: 'rgba(139, 92, 246, 0.15)',
    accentColor: '#8B5CF6',
    title: 'Cria grupos,\ndivide despesas',
    subtitle: 'Adiciona amigos, colegas ou família e regista facilmente todas as despesas partilhadas sem complicações.',
    highlight: 'grupos',
    decorShape: 'circle',
  },
  {
    id: '2',
    icon: 'wallet',
    iconColor: '#10B981',
    iconBg: 'rgba(16, 185, 129, 0.15)',
    accentColor: '#10B981',
    title: 'Saldos sempre\natualizados',
    subtitle: 'O backend calcula automaticamente quem deve a quem. Tens sempre uma visão clara das tuas finanças partilhadas.',
    highlight: 'sempre',
    decorShape: 'diamond',
  },
  {
    id: '3',
    icon: 'checkmark-circle',
    iconColor: '#EC4899',
    iconBg: 'rgba(236, 72, 153, 0.15)',
    accentColor: '#EC4899',
    title: 'Acerta contas\nsem stress',
    subtitle: 'Com um código de convite, qualquer pessoa pode juntar-se ao grupo. Registar despesas é tão simples como 1, 2, 3.',
    highlight: 'contas',
    decorShape: 'triangle',
  },
];

interface OnboardingScreenProps {
  onFinish: () => void;
}

export default function OnboardingScreen({ onFinish }: OnboardingScreenProps) {
  const [currentIndex, setCurrentIndex] = useState(0);
  const flatListRef = useRef<FlatList>(null);
  const scrollX = useRef(new Animated.Value(0)).current;

  const onViewableItemsChanged = useRef(
    ({ viewableItems }: { viewableItems: ViewToken[] }) => {
      if (viewableItems.length > 0) {
        setCurrentIndex(viewableItems[0].index ?? 0);
      }
    }
  ).current;

  const handleNext = () => {
    if (currentIndex < slides.length - 1) {
      flatListRef.current?.scrollToIndex({ index: currentIndex + 1, animated: true });
    } else {
      onFinish();
    }
  };

  const handleSkip = () => {
    onFinish();
  };

  const renderItem = ({ item, index }: { item: OnboardingSlide; index: number }) => {
    return (
      <View style={styles.slide}>
        {/* Formas decorativas de fundo */}
        <View style={[styles.decorShape, styles.decorTop, { backgroundColor: `${item.accentColor}10` }]} />
        <View style={[styles.decorShape, styles.decorBottom, { backgroundColor: `${item.accentColor}08` }]} />

        {/* Ícone Principal */}
        <View style={styles.illustrationContainer}>
          {/* Círculo exterior decorativo */}
          <View style={[styles.iconRingOuter, { borderColor: `${item.accentColor}20` }]} />
          <View style={[styles.iconRingInner, { borderColor: `${item.accentColor}30` }]} />

          {/* Ícone central */}
          <View style={[styles.iconContainer, { backgroundColor: item.iconBg }]}>
            <Ionicons name={item.icon} size={64} color={item.iconColor} />
          </View>

          {/* Badges flutuantes decorativos */}
          <View style={[styles.floatingBadge, styles.floatingBadge1, { backgroundColor: item.iconBg, borderColor: `${item.accentColor}30` }]}>
            <Ionicons name="checkmark" size={14} color={item.iconColor} />
          </View>
          <View style={[styles.floatingBadge, styles.floatingBadge2, { backgroundColor: item.iconBg, borderColor: `${item.accentColor}30` }]}>
            <Text style={[styles.floatingBadgeText, { color: item.iconColor }]}>€</Text>
          </View>
          <View style={[styles.floatingBadge, styles.floatingBadge3, { backgroundColor: item.iconBg, borderColor: `${item.accentColor}30` }]}>
            <Ionicons name="arrow-forward" size={12} color={item.iconColor} />
          </View>
        </View>

        {/* Texto */}
        <View style={styles.textContainer}>
          <Text style={styles.slideTitle}>{item.title}</Text>
          <Text style={styles.slideSubtitle}>{item.subtitle}</Text>
        </View>
      </View>
    );
  };

  // Barra de progresso animada
  const progressWidth = scrollX.interpolate({
    inputRange: [0, width * (slides.length - 1)],
    outputRange: [`${100 / slides.length}%`, '100%'],
    extrapolate: 'clamp',
  });

  const currentSlide = slides[currentIndex];
  const isLast = currentIndex === slides.length - 1;

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="transparent" translucent />

      {/* Botão Skip */}
      {!isLast && (
        <TouchableOpacity style={styles.skipButton} onPress={handleSkip}>
          <Text style={styles.skipText}>Saltar</Text>
        </TouchableOpacity>
      )}

      {/* Slides */}
      <Animated.FlatList
        ref={flatListRef}
        data={slides}
        renderItem={renderItem}
        keyExtractor={(item) => item.id}
        horizontal
        pagingEnabled
        showsHorizontalScrollIndicator={false}
        onScroll={Animated.event(
          [{ nativeEvent: { contentOffset: { x: scrollX } } }],
          { useNativeDriver: false }
        )}
        onViewableItemsChanged={onViewableItemsChanged}
        viewabilityConfig={{ viewAreaCoveragePercentThreshold: 50 }}
        scrollEventThrottle={16}
        style={styles.flatList}
      />

      {/* Rodapé */}
      <View style={styles.footer}>
        {/* Dots de indicador */}
        <View style={styles.dotsContainer}>
          {slides.map((_, index) => {
            const dotWidth = scrollX.interpolate({
              inputRange: [
                width * (index - 1),
                width * index,
                width * (index + 1),
              ],
              outputRange: [8, 24, 8],
              extrapolate: 'clamp',
            });
            const dotOpacity = scrollX.interpolate({
              inputRange: [
                width * (index - 1),
                width * index,
                width * (index + 1),
              ],
              outputRange: [0.3, 1, 0.3],
              extrapolate: 'clamp',
            });

            return (
              <Animated.View
                key={index}
                style={[
                  styles.dot,
                  {
                    width: dotWidth,
                    opacity: dotOpacity,
                    backgroundColor: currentSlide.accentColor,
                  },
                ]}
              />
            );
          })}
        </View>

        {/* Barra de progresso linear */}
        <View style={styles.progressBarTrack}>
          <Animated.View
            style={[
              styles.progressBarFill,
              {
                width: progressWidth,
                backgroundColor: currentSlide.accentColor,
              },
            ]}
          />
        </View>

        {/* Botão de avançar */}
        <TouchableOpacity
          style={[styles.nextButton, { backgroundColor: currentSlide.accentColor }]}
          onPress={handleNext}
          activeOpacity={0.85}
        >
          {isLast ? (
            <>
              <Text style={styles.nextButtonText}>Começar agora</Text>
              <Ionicons name="rocket-outline" size={20} color={theme.colors.white} style={{ marginLeft: 8 }} />
            </>
          ) : (
            <>
              <Text style={styles.nextButtonText}>Seguinte</Text>
              <Ionicons name="arrow-forward" size={20} color={theme.colors.white} style={{ marginLeft: 8 }} />
            </>
          )}
        </TouchableOpacity>

        {/* Link de login para quem já tem conta */}
        <View style={styles.loginRow}>
          <Text style={styles.loginText}>Já tens conta? </Text>
          <TouchableOpacity onPress={onFinish}>
            <Text style={[styles.loginLink, { color: currentSlide.accentColor }]}>Entrar</Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
}

const ICON_CONTAINER_SIZE = 140;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.background,
  },
  skipButton: {
    position: 'absolute',
    top: 56,
    right: 24,
    zIndex: 10,
    paddingHorizontal: 16,
    paddingVertical: 8,
    backgroundColor: theme.colors.surface,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  skipText: {
    color: theme.colors.textMuted,
    fontSize: 13,
    fontWeight: '600',
  },
  flatList: {
    flex: 1,
  },
  slide: {
    width,
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 32,
    paddingTop: 60,
  },
  // Formas decorativas
  decorShape: {
    position: 'absolute',
    borderRadius: 9999,
  },
  decorTop: {
    width: width * 0.8,
    height: width * 0.8,
    top: -width * 0.2,
    right: -width * 0.2,
  },
  decorBottom: {
    width: width * 0.6,
    height: width * 0.6,
    bottom: height * 0.15,
    left: -width * 0.2,
  },
  // Ilustração
  illustrationContainer: {
    position: 'relative',
    width: ICON_CONTAINER_SIZE + 80,
    height: ICON_CONTAINER_SIZE + 80,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 48,
  },
  iconRingOuter: {
    position: 'absolute',
    width: ICON_CONTAINER_SIZE + 60,
    height: ICON_CONTAINER_SIZE + 60,
    borderRadius: (ICON_CONTAINER_SIZE + 60) / 2,
    borderWidth: 1.5,
  },
  iconRingInner: {
    position: 'absolute',
    width: ICON_CONTAINER_SIZE + 20,
    height: ICON_CONTAINER_SIZE + 20,
    borderRadius: (ICON_CONTAINER_SIZE + 20) / 2,
    borderWidth: 1.5,
  },
  iconContainer: {
    width: ICON_CONTAINER_SIZE,
    height: ICON_CONTAINER_SIZE,
    borderRadius: ICON_CONTAINER_SIZE * 0.3,
    alignItems: 'center',
    justifyContent: 'center',
  },
  // Badges flutuantes
  floatingBadge: {
    position: 'absolute',
    width: 36,
    height: 36,
    borderRadius: 18,
    borderWidth: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  floatingBadge1: {
    top: 8,
    right: 12,
  },
  floatingBadge2: {
    bottom: 16,
    left: 8,
  },
  floatingBadge3: {
    top: 40,
    left: 4,
  },
  floatingBadgeText: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  // Texto
  textContainer: {
    alignItems: 'center',
  },
  slideTitle: {
    fontSize: 32,
    fontWeight: 'bold',
    color: theme.colors.text,
    textAlign: 'center',
    lineHeight: 40,
    marginBottom: 16,
  },
  slideSubtitle: {
    fontSize: 16,
    color: theme.colors.textMuted,
    textAlign: 'center',
    lineHeight: 25,
    paddingHorizontal: 8,
  },
  // Rodapé
  footer: {
    paddingHorizontal: 28,
    paddingBottom: 40,
    paddingTop: 16,
    alignItems: 'center',
  },
  dotsContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 20,
    gap: 6,
  },
  dot: {
    height: 8,
    borderRadius: 4,
  },
  progressBarTrack: {
    width: '100%',
    height: 3,
    backgroundColor: theme.colors.border,
    borderRadius: 2,
    marginBottom: 28,
    overflow: 'hidden',
  },
  progressBarFill: {
    height: '100%',
    borderRadius: 2,
  },
  nextButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
    height: 56,
    borderRadius: theme.borderRadius.md,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.3,
    shadowRadius: 12,
    elevation: 8,
  },
  nextButtonText: {
    color: theme.colors.white,
    fontSize: 17,
    fontWeight: 'bold',
  },
  loginRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  loginText: {
    color: theme.colors.textMuted,
    fontSize: 14,
  },
  loginLink: {
    fontSize: 14,
    fontWeight: 'bold',
  },
});
