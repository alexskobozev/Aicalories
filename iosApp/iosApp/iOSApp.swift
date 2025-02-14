import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
            KoinManager().doInitKoin()
        }
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
