/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useState, useEffect } from "react";
import {
  Form,
  Input,
  Modal,
  Select,
  Row,
  Col,
  message,
} from "antd";
import { getStudyPrograms } from "@/api/studyProgram";
import { reqUserInfo } from "@/api/user";

const { Option } = Select;

const AddKelasForm = ({ visible, onCancel, onOk, confirmLoading }) => {
  const [form] = Form.useForm();
  const [userStudyProgramId, setUserStudyProgramId] = useState("");
  const [studyProgramList, setStudyProgramList] = useState([]);

  const fetchUserInfo = async () => {
    try {
      const response = await reqUserInfo();
      setUserStudyProgramId(response.data.school_id);
    } catch (error) {
      message.error("Gagal mengambil informasi pengguna");
    }
  };

  const fetchStudyProgramList = async () => {
    try {
      const result = await getStudyPrograms();
      if (result.data.statusCode === 200) {
        setStudyProgramList(result.data.content || []);
      } else {
        message.error("Gagal mengambil data");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  useEffect(() => {
    fetchUserInfo();
    fetchStudyProgramList();
  }, []);

  useEffect(() => {
    if (userStudyProgramId) {
      form.setFieldsValue({ idStudyProgram: userStudyProgramId });
    }
  }, [userStudyProgramId, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      onOk(values);
    } catch (error) {
      console.error("Validation failed:", error);
    }
  };

  return (
    <Modal
      title="Tambah Kelas"
      open={visible}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
      okText="Simpan"
      width={500}
    >
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col xs={24} sm={24} md={24}>
            <Form.Item
              label="Program Studi:"
              name="idStudyProgram"
              style={{ display: "none" }}
              rules={[{ required: true, message: "Silahkan pilih Program Studi" }]}
            >
              <Select defaultValue={userStudyProgramId} disabled>
                {studyProgramList
                  .filter(({ id }) => id === userStudyProgramId)
                  .map(({ id, name }) => (
                    <Option key={id} value={id}>
                      {name}
                    </Option>
                  ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={24}>
            <Form.Item
              label="Kelas :"
              name="namaKelas"
              rules={[{ required: true, message: "Silahkan isikan Kelas" }]}
            >
              <Input placeholder="Kelas " />
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={24}>
            <Form.Item
              label="Angkatan :"
              name="angkatan"
              rules={[{ required: true, message: "Silahkan isikan Angkatan" }]}
            >
              <Input placeholder="Contoh: 2023" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};

export default AddKelasForm;
