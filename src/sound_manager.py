import pygame

class SoundManager:
    def __init__(self):
        pygame.mixer.init()
        self.sounds = {
            'collect_cat': self.create_arpeggio([440, 554, 659], 0.05),
            'get_caught': self.create_arpeggio([330, 220, 165], 0.07),
            'key_spawn': self.create_arpeggio([523, 659, 784, 1046], 0.08),
            'win': self.create_arpeggio([523, 659, 784, 1046, 1046], 0.1),
            'i_see_you': self.create_arpeggio([261, 392, 392, 440, 392, 493, 523], 0.08)
        }
        self.background_music_loaded = False
        self.chase_music_loaded = False
        self.load_music()

    def create_arpeggio(self, notes, duration_per_note):
        """Crea un efecto de sonido de arpegio rápido al estilo 8-bits."""
        sample_rate = 44100
        all_samples = bytearray()
        for freq in notes:
            n_samples = int(round(duration_per_note * sample_rate))
            buf = bytearray([int(128 + 127 * (2 * (i / sample_rate * freq - int(i / sample_rate * freq)) - 1)) for i in range(n_samples)])
            all_samples.extend(buf)
        return pygame.mixer.Sound(buffer=all_samples)

    def play_sfx(self, name):
        if name in self.sounds:
            self.sounds[name].play()

    def load_music(self):
        """Carga la música MIDI desde la carpeta assets."""
        try:
            pygame.mixer.music.load('assets/background.mid')
            self.background_music_loaded = True
            print("Música de fondo (MIDI) cargada.")
        except pygame.error:
            print("No se encontró assets/background.mid. La música de fondo estará desactivada.")
        
        try:
            open('assets/chase.mid')
            self.chase_music_loaded = True
            print("Música de persecución (MIDI) encontrada.")
        except FileNotFoundError:
            print("No se encontró assets/chase.mid. La música de persecución estará desactivada.")

    def play_background_music(self):
        if self.background_music_loaded:
            pygame.mixer.music.load('assets/background.mid')
            pygame.mixer.music.play(-1)

    def play_chase_music(self):
        if self.chase_music_loaded:
            pygame.mixer.music.load('assets/chase.mid')
            pygame.mixer.music.play(-1)
            print("Música de persecución (MIDI) iniciada.")

    def stop_chase_music(self):
        # Vuelve a la música de fondo normal
        pygame.mixer.music.stop()
        if self.background_music_loaded:
            self.play_background_music()
