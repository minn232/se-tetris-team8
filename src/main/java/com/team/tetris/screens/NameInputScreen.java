/*
    이름을 입력하는 창을 구현하는 클래스.
    게임 오버 후 최종 점수가 랭킹에 들었을 때 호출됨.
    축하메세지, 이름 입력 필드, 확인 버튼으로 구성됨.
    이름 입력 필드에는 replace()메소드를 @override하여 최대 10글자까지 입력 가능하며, 알파벳 대소문자만 허용됨.
    확인 버튼을 누르면 submitScore() 메소드가 호출되어 랭킹 매니저에 새로운 랭킹 데이터를 추가하고 이름 입력 창을 닫음.
 */





package com.team.tetris.screens;

import java.awt.Component;
import java.time.LocalDateTime;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.team.tetris.ranking.RankingEntry;
import com.team.tetris.ranking.RankingManager;

//이름을 입력하는 창을 구현한 클래스
public class NameInputScreen extends JFrame {

    //최종 점수와 이름 입력 필드의 변수 선언
    private final int finalScore;
    private JTextField nameField;

    //생성자. 게임 클래스에서 최종점수를 받아와야한다.
    public NameInputScreen(int finalScore) {
        this.finalScore = finalScore;
        initializeUI();
    }

    //이름 입력 UI
    private void initializeUI() {
        //창 설정
        setTitle("새로운 기록!");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //메인 패널 설정
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //축하 메세지, 이름 입력, 확인 버튼 생성
        JLabel messageLabel = new JLabel("축하합니다! 상위 10위 안에 들었습니다!");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //이름 입력 필드 생성
        nameField = new JTextField(10);
        // 최대 10글자 제한을 실시간으로 적용
        ((AbstractDocument) nameField.getDocument()).setDocumentFilter(new DocumentFilter() {

            //텍스트의 삽입/수정/삭제 시 호출되는 replace 메소드에 조건 추가. 알파벳 10글자 제한.
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (newText.length() <= 10 && text.matches("[a-zA-Z]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        //확인 버튼 추가. submitScore 메소드 호출
        JButton submitButton = new JButton("확인");
        submitButton.addActionListener(e -> submitScore());

        //컴포넌트 배치
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(nameField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(submitButton);

        add(mainPanel);
    }

    //확인 버튼을 누를 시 실행되는 메소드
    private void submitScore() {
        //nameField에서 이름을 받아와 앞뒤공백제거.
        String playerName = nameField.getText().trim();
        //이름이 비어있지 않으면 rankingManager에 새로운 랭킹데이터 추가.
        if (!playerName.isEmpty()) {
            RankingManager.getInstance().addEntry(new RankingEntry(
                playerName,
                finalScore,
                LocalDateTime.now()
            ));
            dispose();  //이름 입력 창 닫기
            //게임 오버 화면 열기
            new GameOverScreen(finalScore).setVisible(true);
            //이름 입력이 비어있으면 경고창 표시
        } else {
            JOptionPane.showMessageDialog(this, "이름을 입력해주세요!");
        }
    }
}