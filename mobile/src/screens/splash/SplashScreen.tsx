import React, { useEffect, useRef } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Animated,
  StatusBar,
  Dimensions,
} from 'react-native';
import { theme } from '../../styles/theme';

const { width, height } = Dimensions.get('window');

interface SplashScreenProps {
  onFinish: () => void;
}

export default function SplashScreen({ onFinish }: SplashScreenProps) {
  const logoScale = useRef(new Animated.Value(0.3)).current;
  const logoOpacity = useRef(new Animated.Value(0)).current;
  const titleOpacity = useRef(new Animated.Value(0)).current;
  const subtitleOpacity = useRef(new Animated.Value(0)).current;
  const ring1Scale = useRef(new Animated.Value(0.5)).current;
  const ring1Opacity = useRef(new Animated.Value(0.6)).current;
  const ring2Scale = useRef(new Animated.Value(0.5)).current;
  const ring2Opacity = useRef(new Animated.Value(0.4)).current;

  useEffect(() => {
    // Animação dos anéis (pulse/glow)
    const pulseRings = () => {
      Animated.loop(
        Animated.parallel([
          Animated.sequence([
            Animated.timing(ring1Scale, { toValue: 1.4, duration: 1200, useNativeDriver: true }),
            Animated.timing(ring1Scale, { toValue: 0.9, duration: 1200, useNativeDriver: true }),
          ]),
          Animated.sequence([
            Animated.timing(ring1Opacity, { toValue: 0, duration: 1200, useNativeDriver: true }),
            Animated.timing(ring1Opacity, { toValue: 0.5, duration: 1200, useNativeDriver: true }),
          ]),
        ])
      ).start();

      Animated.loop(
        Animated.parallel([
          Animated.sequence([
            Animated.timing(ring2Scale, { toValue: 1.8, duration: 1600, useNativeDriver: true }),
            Animated.timing(ring2Scale, { toValue: 1.0, duration: 1600, useNativeDriver: true }),
          ]),
          Animated.sequence([
            Animated.timing(ring2Opacity, { toValue: 0, duration: 1600, useNativeDriver: true }),
            Animated.timing(ring2Opacity, { toValue: 0.3, duration: 1600, useNativeDriver: true }),
          ]),
        ])
      ).start();
    };

    // Sequência principal de entrada
    Animated.sequence([
      // Logo aparece com bounce
      Animated.parallel([
        Animated.spring(logoScale, {
          toValue: 1,
          friction: 5,
          tension: 100,
          useNativeDriver: true,
        }),
        Animated.timing(logoOpacity, {
          toValue: 1,
          duration: 500,
          useNativeDriver: true,
        }),
      ]),
      // Título aparece
      Animated.timing(titleOpacity, {
        toValue: 1,
        duration: 400,
        useNativeDriver: true,
      }),
      // Subtítulo aparece
      Animated.timing(subtitleOpacity, {
        toValue: 1,
        duration: 400,
        useNativeDriver: true,
      }),
      // Aguardar 1.2s antes de sair
      Animated.delay(1200),
    ]).start(() => {
      onFinish();
    });

    pulseRings();
  }, []);

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="transparent" translucent />

      {/* Fundo com gradiente via sobreposição de camadas */}
      <View style={styles.bgCircle1} />
      <View style={styles.bgCircle2} />
      <View style={styles.bgCircle3} />

      {/* Anéis de pulso */}
      <Animated.View
        style={[
          styles.pulseRing,
          styles.pulseRing1,
          { transform: [{ scale: ring1Scale }], opacity: ring1Opacity },
        ]}
      />
      <Animated.View
        style={[
          styles.pulseRing,
          styles.pulseRing2,
          { transform: [{ scale: ring2Scale }], opacity: ring2Opacity },
        ]}
      />

      {/* Logo + Nome */}
      <Animated.View
        style={[
          styles.logoContainer,
          { transform: [{ scale: logoScale }], opacity: logoOpacity },
        ]}
      >
        {/* Logo Apportion - Ícone de Divisão Estilizado */}
        <View style={styles.logoBadge}>
          {/* Ícone SVG-like com View components */}
          <View style={styles.pieContainer}>
            {/* Slice 1 - Violeta */}
            <View style={[styles.pieSlice, styles.pieSlice1]} />
            {/* Slice 2 - Magenta */}
            <View style={[styles.pieSlice, styles.pieSlice2]} />
            {/* Slice 3 - Esmeralda */}
            <View style={[styles.pieSlice, styles.pieSlice3]} />
            {/* Centro */}
            <View style={styles.pieCenter} />
            {/* Linha divisória horizontal */}
            <View style={styles.dividerH} />
            {/* Linha divisória vertical */}
            <View style={styles.dividerV} />
          </View>
        </View>
      </Animated.View>

      {/* Nome da App */}
      <Animated.Text style={[styles.appName, { opacity: titleOpacity }]}>
        Apportion
      </Animated.Text>

      {/* Tagline */}
      <Animated.Text style={[styles.tagline, { opacity: subtitleOpacity }]}>
        Divide. Simplifica. Vive.
      </Animated.Text>

      {/* Rodapé */}
      <Animated.Text style={[styles.footer, { opacity: subtitleOpacity }]}>
        by Apportion Labs
      </Animated.Text>
    </View>
  );
}

const LOGO_SIZE = 100;
const RING_SIZE = 180;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.background,
    alignItems: 'center',
    justifyContent: 'center',
  },
  // Camadas de fundo para simular gradiente radial
  bgCircle1: {
    position: 'absolute',
    width: width * 1.2,
    height: width * 1.2,
    borderRadius: width * 0.6,
    backgroundColor: 'rgba(139, 92, 246, 0.08)',
    top: -width * 0.3,
    left: -width * 0.1,
  },
  bgCircle2: {
    position: 'absolute',
    width: width * 0.8,
    height: width * 0.8,
    borderRadius: width * 0.4,
    backgroundColor: 'rgba(16, 185, 129, 0.05)',
    bottom: -width * 0.1,
    right: -width * 0.2,
  },
  bgCircle3: {
    position: 'absolute',
    width: width * 0.6,
    height: width * 0.6,
    borderRadius: width * 0.3,
    backgroundColor: 'rgba(236, 72, 153, 0.04)',
    bottom: height * 0.1,
    left: -width * 0.1,
  },
  // Anéis pulsantes
  pulseRing: {
    position: 'absolute',
    borderRadius: RING_SIZE / 2,
    borderWidth: 1.5,
  },
  pulseRing1: {
    width: RING_SIZE,
    height: RING_SIZE,
    borderColor: 'rgba(139, 92, 246, 0.6)',
  },
  pulseRing2: {
    width: RING_SIZE * 1.4,
    height: RING_SIZE * 1.4,
    borderColor: 'rgba(139, 92, 246, 0.3)',
  },
  // Logo
  logoContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 24,
  },
  logoBadge: {
    width: LOGO_SIZE,
    height: LOGO_SIZE,
    borderRadius: LOGO_SIZE * 0.28,
    backgroundColor: theme.colors.surface,
    borderWidth: 1.5,
    borderColor: 'rgba(139, 92, 246, 0.4)',
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: theme.colors.primary,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.5,
    shadowRadius: 20,
    elevation: 20,
  },
  // Ícone de "Pie Chart / Divisão"
  pieContainer: {
    width: 56,
    height: 56,
    borderRadius: 28,
    overflow: 'hidden',
    position: 'relative',
    backgroundColor: '#1D1A30',
  },
  pieSlice: {
    position: 'absolute',
    width: '50%',
    height: '50%',
  },
  pieSlice1: {
    top: 0,
    left: 0,
    backgroundColor: '#8B5CF6',
    borderBottomRightRadius: 0,
  },
  pieSlice2: {
    top: 0,
    right: 0,
    backgroundColor: '#EC4899',
  },
  pieSlice3: {
    bottom: 0,
    left: 0,
    width: '100%',
    backgroundColor: '#10B981',
    opacity: 0.85,
  },
  pieCenter: {
    position: 'absolute',
    width: 22,
    height: 22,
    borderRadius: 11,
    backgroundColor: theme.colors.surface,
    top: '50%',
    left: '50%',
    transform: [{ translateX: -11 }, { translateY: -11 }],
    zIndex: 10,
  },
  dividerH: {
    position: 'absolute',
    width: '100%',
    height: 2,
    backgroundColor: theme.colors.surface,
    top: '50%',
    transform: [{ translateY: -1 }],
    zIndex: 5,
  },
  dividerV: {
    position: 'absolute',
    width: 2,
    height: '100%',
    backgroundColor: theme.colors.surface,
    left: '50%',
    transform: [{ translateX: -1 }],
    zIndex: 5,
  },
  // Textos
  appName: {
    fontSize: 42,
    fontWeight: 'bold',
    color: theme.colors.text,
    letterSpacing: 1,
    marginBottom: 10,
  },
  tagline: {
    fontSize: 16,
    color: theme.colors.textMuted,
    letterSpacing: 0.5,
    marginBottom: 8,
  },
  footer: {
    position: 'absolute',
    bottom: 48,
    fontSize: 12,
    color: 'rgba(156, 163, 175, 0.4)',
    letterSpacing: 1,
    textTransform: 'uppercase',
  },
});
